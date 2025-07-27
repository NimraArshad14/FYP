    package com.mbg5.classroommanagementsystem

    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.mbg5.classroommanagementsystem.network.ApiClient
    import com.mbg5.classroommanagementsystem.network.ClassroomRequest
    import com.mbg5.classroommanagementsystem.network.ClassroomResponse
    import com.mbg5.classroommanagementsystem.network.StudentResponse
    import com.mbg5.classroommanagementsystem.network.TeacherResponse
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.launch

    // LIST STATE
    sealed class ClassroomUiState {
        object Loading : ClassroomUiState()
        data class Success(val classes: List<ClassroomResponse>) : ClassroomUiState()
        data class Error(val message: String) : ClassroomUiState()
    }

    // CREATE STATE
    sealed class CreateClassState {
        object Idle : CreateClassState()
        object Loading : CreateClassState()
        data class Success(val created: ClassroomResponse) : CreateClassState()
        data class Error(val message: String) : CreateClassState()
    }

    // UPDATE STATE
    sealed class UpdateClassState {
        object Idle : UpdateClassState()
        object Loading : UpdateClassState()
        data class Success(val updated: ClassroomResponse) : UpdateClassState()
        data class Error(val message: String) : UpdateClassState()
    }

    // DELETE STATE
    sealed class DeleteClassState {
        object Idle : DeleteClassState()
        object Loading : DeleteClassState()
        data class Success(val id: String) : DeleteClassState()
        data class Error(val message: String) : DeleteClassState()
    }

    class ClassroomViewModel : ViewModel() {

        private val api = ApiClient.apiService

        // dropdown data
        private val _teachers = MutableStateFlow<List<TeacherResponse>>(emptyList())
        val teachers: StateFlow<List<TeacherResponse>> = _teachers

        private val _students = MutableStateFlow<List<StudentResponse>>(emptyList())
        val students: StateFlow<List<StudentResponse>> = _students

        // list state
        private val _listState = MutableStateFlow<ClassroomUiState>(ClassroomUiState.Loading)
        val listState: StateFlow<ClassroomUiState> = _listState

        // create / update / delete states
        private val _createState = MutableStateFlow<CreateClassState>(CreateClassState.Idle)
        val createState: StateFlow<CreateClassState> = _createState

        private val _updateState = MutableStateFlow<UpdateClassState>(UpdateClassState.Idle)
        val updateState: StateFlow<UpdateClassState> = _updateState

        private val _deleteState = MutableStateFlow<DeleteClassState>(DeleteClassState.Idle)
        val deleteState: StateFlow<DeleteClassState> = _deleteState

        init {
            // seed dropdowns & initial list
            viewModelScope.launch {
                api.listTeachers().body()?.let { _teachers.value = it }
                api.listStudents().body()?.let { _students.value = it }
                loadClasses()
            }
        }

        fun loadClasses() = viewModelScope.launch {
            _listState.value = ClassroomUiState.Loading
            try {
                val resp = api.listClasses()
                if (resp.isSuccessful) {
                    _listState.value = ClassroomUiState.Success(resp.body()!!)
                } else {
                    _listState.value = ClassroomUiState.Error(resp.errorBody()?.string() ?: "Unknown error")
                }
            } catch (e: Exception) {
                _listState.value = ClassroomUiState.Error(e.localizedMessage ?: "Network error")
            }
        }

        fun createClass(name: String, teacherId: String, studentIds: List<String>) = viewModelScope.launch {
            _createState.value = CreateClassState.Loading
            try {
                val req = ClassroomRequest(name, teacherId, studentIds)
                val resp = api.createClass(req)
                if (resp.isSuccessful) {
                    _createState.value = CreateClassState.Success(resp.body()!!)
                    loadClasses()
                } else {
                    _createState.value = CreateClassState.Error(resp.errorBody()?.string() ?: "Unknown error")
                }
            } catch (e: Exception) {
                _createState.value = CreateClassState.Error(e.localizedMessage ?: "Network error")
            }
        }

        fun updateClass(id: String, name: String, teacherId: String, studentIds: List<String>) = viewModelScope.launch {
            _updateState.value = UpdateClassState.Loading
            try {
                val req = ClassroomRequest(name, teacherId, studentIds)
                val resp = api.updateClass(id, req)
                if (resp.isSuccessful) {
                    _updateState.value = UpdateClassState.Success(resp.body()!!)
                    loadClasses()
                } else {
                    _updateState.value = UpdateClassState.Error(resp.errorBody()?.string() ?: "Unknown error")
                }
            } catch (e: Exception) {
                _updateState.value = UpdateClassState.Error(e.localizedMessage ?: "Network error")
            }
        }

        fun deleteClass(id: String) = viewModelScope.launch {
            _deleteState.value = DeleteClassState.Loading
            try {
                val resp = api.deleteClass(id)
                if (resp.isSuccessful) {
                    _deleteState.value = DeleteClassState.Success(id)
                    loadClasses()
                } else {
                    _deleteState.value = DeleteClassState.Error(resp.errorBody()?.string() ?: "Unknown error")
                }
            } catch (e: Exception) {
                _deleteState.value = DeleteClassState.Error(e.localizedMessage ?: "Network error")
            }
        }

        fun resetCreate() { _createState.value = CreateClassState.Idle }
        fun resetUpdate() { _updateState.value = UpdateClassState.Idle }
        fun resetDelete() { _deleteState.value = DeleteClassState.Idle }
    }
