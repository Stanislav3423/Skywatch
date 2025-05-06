function loadReports() {
    const token = localStorage.getItem('jwt');
    fetch('/reports/all-for-user', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    })
        .then(response => response.json())
        .then(reports => {
            const reportsList = document.getElementById('reportsList');
            reportsList.innerHTML = '';
            reports.forEach(report => {
                const reportElement = document.createElement('div');
                reportElement.classList.add('report-item');
                reportElement.innerHTML = `
                    <h3>${report.name}</h3>
                    <p>Date Created: ${new Date(report.createdAt).toLocaleString()}</p>
                    <div class="report-actions">
                        <button class="report-btn download" onclick="downloadReport(${report.id})">
                            <i data-lucide="download"></i>
                        </button>
                        <button class="report-btn delete" onclick="deleteReport(${report.id})">
                            <i data-lucide="trash-2"></i>
                        </button>
                    </div>
                    `;
                reportsList.appendChild(reportElement);
                lucide.createIcons();
            });
        })
        .catch(error => {
            console.error('Error loading reports:', error);
            alert('Error loading reports.');
        });
}

function downloadReport(reportId) {
    fetch(`/reports/download/${reportId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => response.json())
        .then(data => {
            const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `report_${reportId}.json`;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
        })
        .catch(error => {
            console.error('Error downloading report:', error);
            alert('Error downloading report.');
        });
}

function deleteReport(reportId) {
    fetch(`/reports/delete/${reportId}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => {
            if (response.ok) {
                alert('Report successfully deleted!');
                loadReports(); // Перезавантажуємо список звітів після видалення
            } else {
                alert('Failed to delete report.');
            }
        })
        .catch(error => {
            console.error('Error deleting report:', error);
            alert('Error deleting report.');
        });
}

window.onload = loadReports;