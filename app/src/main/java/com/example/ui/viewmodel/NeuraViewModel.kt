package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.core.components.NeuraTab
import com.example.data.local.entities.ConversationEntity
import com.example.data.local.entities.CreationEntity
import com.example.data.local.entities.MemoryEntity
import com.example.data.local.entities.MessageEntity
import com.example.data.repository.ConnectionTestResult
import com.example.data.repository.NeuraRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NeuraViewModel(private val repository: NeuraRepository) : ViewModel() {

    val settingsManager = repository.settingsManager

    private val _currentTab = MutableStateFlow(NeuraTab.CHAT)
    val currentTab: StateFlow<NeuraTab> = _currentTab.asStateFlow()

    private val _currentConversationId = MutableStateFlow<String?>(null)
    val currentConversationId: StateFlow<String?> = _currentConversationId.asStateFlow()

    val conversations: StateFlow<List<ConversationEntity>> = repository.allConversations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val memories: StateFlow<List<MemoryEntity>> = repository.allMemories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val creations: StateFlow<List<CreationEntity>> = repository.allCreations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentMessages: StateFlow<List<MessageEntity>> = _currentConversationId
        .flatMapLatest { id ->
            if (id != null) repository.getMessagesForConversation(id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _isThinking = MutableStateFlow(false)
    val isThinking: StateFlow<Boolean> = _isThinking.asStateFlow()

    private val _attachedFile = MutableStateFlow<Pair<String, String>?>(null)
    val attachedFile: StateFlow<Pair<String, String>?> = _attachedFile.asStateFlow()

    private val _showAttachmentDialog = MutableStateFlow(false)
    val showAttachmentDialog: StateFlow<Boolean> = _showAttachmentDialog.asStateFlow()

    private val _showVoiceDialog = MutableStateFlow(false)
    val showVoiceDialog: StateFlow<Boolean> = _showVoiceDialog.asStateFlow()

    private val _connectionTestResult = MutableStateFlow<ConnectionTestResult?>(null)
    val connectionTestResult: StateFlow<ConnectionTestResult?> = _connectionTestResult.asStateFlow()

    private val _isTestingConnection = MutableStateFlow(false)
    val isTestingConnection: StateFlow<Boolean> = _isTestingConnection.asStateFlow()

    private val _agnesConnectionResult = MutableStateFlow<ConnectionTestResult?>(null)
    val agnesConnectionResult: StateFlow<ConnectionTestResult?> = _agnesConnectionResult.asStateFlow()

    private val _isTestingAgnes = MutableStateFlow(false)
    val isTestingAgnes: StateFlow<Boolean> = _isTestingAgnes.asStateFlow()

    private val _creationsFilter = MutableStateFlow("TOUS")
    val creationsFilter: StateFlow<String> = _creationsFilter.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    fun setTab(tab: NeuraTab) {
        _currentTab.value = tab
    }

    fun setInputText(text: String) {
        _inputText.value = text
    }

    fun openAttachmentDialog() {
        _showAttachmentDialog.value = true
    }

    fun closeAttachmentDialog() {
        _showAttachmentDialog.value = false
    }

    fun openVoiceDialog() {
        _showVoiceDialog.value = true
    }

    fun closeVoiceDialog() {
        _showVoiceDialog.value = false
    }

    fun attachFile(name: String, type: String) {
        _attachedFile.value = Pair(name, type)
    }

    fun removeAttachment() {
        _attachedFile.value = null
    }

    fun setCreationsFilter(filter: String) {
        _creationsFilter.value = filter
    }

    fun selectConversation(id: String) {
        _currentConversationId.value = id
        _currentTab.value = NeuraTab.CHAT
    }

    fun startNewConversation() {
        viewModelScope.launch {
            val newId = repository.createNewConversation("Nouvelle session NEURA")
            _currentConversationId.value = newId
            _attachedFile.value = null
            _inputText.value = ""
            _currentTab.value = NeuraTab.CHAT
        }
    }

    fun deleteConversation(id: String) {
        viewModelScope.launch {
            repository.deleteConversation(id)
            if (_currentConversationId.value == id) {
                _currentConversationId.value = null
            }
        }
    }

    fun sendMessage(customPrompt: String? = null) {
        val prompt = (customPrompt ?: _inputText.value).trim()
        val attached = _attachedFile.value

        if (prompt.isBlank() && attached == null) return

        val textToSend = if (prompt.isNotBlank()) prompt else "Analyse ce document : ${attached?.first}"

        _inputText.value = ""
        _attachedFile.value = null

        viewModelScope.launch {
            var convId = _currentConversationId.value
            if (convId == null) {
                convId = repository.createNewConversation(textToSend.take(28))
                _currentConversationId.value = convId
            }

            _isThinking.value = true
            try {
                repository.sendMessage(
                    conversationId = convId,
                    userText = textToSend,
                    attachedFileName = attached?.first
                )
            } finally {
                _isThinking.value = false
            }
        }
    }

    fun addMemory(key: String, value: String, category: String, importance: String = "Haute") {
        viewModelScope.launch {
            repository.addMemory(key, value, category, importance)
        }
    }

    fun deleteMemory(id: Long) {
        viewModelScope.launch {
            repository.deleteMemory(id)
        }
    }

    fun deleteCreation(id: String) {
        viewModelScope.launch {
            repository.deleteCreation(id)
        }
    }

    fun testConnection() {
        viewModelScope.launch {
            _isTestingConnection.value = true
            _connectionTestResult.value = null
            try {
                val result = repository.testConnection()
                _connectionTestResult.value = result
            } finally {
                _isTestingConnection.value = false
            }
        }
    }

    fun testAgnesConnection() {
        viewModelScope.launch {
            _isTestingAgnes.value = true
            _agnesConnectionResult.value = null
            try {
                val result = repository.testAgnesConnection()
                _agnesConnectionResult.value = result
            } finally {
                _isTestingAgnes.value = false
            }
        }
    }

    fun updateAgnesSettings(
        endpoint: String,
        apiKey: String,
        model: String
    ) {
        settingsManager.agnesEndpoint = endpoint
        settingsManager.agnesApiKey = apiKey
        settingsManager.agnesModel = model
    }

    fun updateSettings(
        endpoint: String,
        apiKey: String,
        provider: String,
        textModel: String,
        imageModel: String,
        visionModel: String,
        userName: String,
        learningStyle: String
    ) {
        settingsManager.endpoint = endpoint
        settingsManager.apiKey = apiKey
        settingsManager.providerName = provider
        settingsManager.textModel = textModel
        settingsManager.imageModel = imageModel
        settingsManager.visionModel = visionModel
        settingsManager.userName = userName
        settingsManager.learningStyle = learningStyle
    }
}

class NeuraViewModelFactory(private val repository: NeuraRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NeuraViewModel::class.java)) {
            return NeuraViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
