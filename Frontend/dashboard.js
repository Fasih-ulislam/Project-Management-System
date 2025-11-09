document.addEventListener('DOMContentLoaded', () => {
const token = localStorage.getItem("token");
if(!token){
    window.location.href = "login.html";
}



});
  function navigate(role) {
    switch (role) {
      case 'admin':
        window.location.href = 'project.html';
        break;
      case 'manager':
        window.location.href = 'assignedProject.html';
        break;
      case 'member':
        window.location.href = 'assignedTasks.html';
        break;
      default:
        alert('Invalid role!');
    }
  }