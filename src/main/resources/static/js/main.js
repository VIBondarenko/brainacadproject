// Education Control System - Main JavaScript

// Global application namespace
window.ECS = window.ECS || {};

// Application initialization
document.addEventListener('DOMContentLoaded', function() {
    ECS.init();
});

// Main application object
ECS = {
    // Initialize the application
    init: function() {
        this.initTooltips();
        this.initConfirmations();
        this.initFormValidation();
        this.initAutoSave();
        this.initNotifications();
        console.log('ECS Web Application initialized');
    },

    // Initialize Bootstrap tooltips
    initTooltips: function() {
        const tooltips = document.querySelectorAll('[data-bs-toggle="tooltip"]');
        tooltips.forEach(tooltip => {
            new bootstrap.Tooltip(tooltip);
        });
    },

    // Initialize confirmation dialogs
    initConfirmations: function() {
        const confirmButtons = document.querySelectorAll('[data-confirm]');
        confirmButtons.forEach(button => {
            button.addEventListener('click', function(e) {
                const message = this.getAttribute('data-confirm') || 'Are you sure?';
                if (!confirm(message)) {
                    e.preventDefault();
                    return false;
                }
            });
        });
    },

    // Initialize form validation
    initFormValidation: function() {
        const forms = document.querySelectorAll('.needs-validation');
        forms.forEach(form => {
            form.addEventListener('submit', function(event) {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            });
        });
    },

    // Initialize auto-save functionality
    initAutoSave: function() {
        let autoSaveTimeout;
        const autoSaveForms = document.querySelectorAll('[data-autosave]');
        
        autoSaveForms.forEach(form => {
            const inputs = form.querySelectorAll('input, textarea, select');
            inputs.forEach(input => {
                input.addEventListener('input', function() {
                    clearTimeout(autoSaveTimeout);
                    autoSaveTimeout = setTimeout(() => {
                        ECS.autoSave(form);
                    }, 2000); // Auto-save after 2 seconds of inactivity
                });
            });
        });
    },

    // Auto-save form data
    autoSave: function(form) {
        const formData = new FormData(form);
        const data = {};
        
        for (let [key, value] of formData.entries()) {
            data[key] = value;
        }
        
        // Store in localStorage
        const formId = form.getAttribute('id') || 'default_form';
        localStorage.setItem(`autosave_${formId}`, JSON.stringify(data));
        
        // Show auto-save notification
        this.showNotification('Data auto-saved', 'info', 1000);
    },

    // Initialize notifications
    initNotifications: function() {
        // Auto-hide alerts after 5 seconds
        const alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
        alerts.forEach(alert => {
            if (!alert.querySelector('.btn-close')) {
                setTimeout(() => {
                    alert.style.transition = 'opacity 0.3s ease-out';
                    alert.style.opacity = '0';
                    setTimeout(() => {
                        if (alert.parentNode) {
                            alert.remove();
                        }
                    }, 300);
                }, 5000);
            }
        });
    },

    // Show notification
    showNotification: function(message, type = 'info', duration = 3000) {
        const alertDiv = document.createElement('div');
        alertDiv.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
        alertDiv.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
        
        const iconMap = {
            'success': 'check-circle',
            'error': 'exclamation-triangle',
            'warning': 'exclamation-triangle',
            'info': 'info-circle'
        };
        
        alertDiv.innerHTML = `
            <i class="bi bi-${iconMap[type] || 'info-circle'}"></i> ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        `;
        
        document.body.appendChild(alertDiv);
        
        // Auto-remove after duration
        setTimeout(() => {
            if (alertDiv.parentNode) {
                alertDiv.style.opacity = '0';
                setTimeout(() => {
                    alertDiv.remove();
                }, 300);
            }
        }, duration);
    },

    // AJAX form submission
    submitForm: function(form, successCallback, errorCallback) {
        const submitBtn = form.querySelector('button[type="submit"]');
        const originalText = submitBtn ? submitBtn.innerHTML : '';
        
        if (submitBtn) {
            submitBtn.innerHTML = '<i class="bi bi-arrow-clockwise spinner-border-sm"></i> Saving...';
            submitBtn.disabled = true;
        }
        
        const formData = new FormData(form);
        
        fetch(form.action || window.location.href, {
            method: form.method || 'POST',
            body: formData,
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
        })
        .then(data => {
            if (data.success) {
                this.showNotification(data.message || 'Operation completed successfully', 'success');
                if (successCallback) successCallback(data);
            } else {
                throw new Error(data.message || 'Operation failed');
            }
        })
        .catch(error => {
            this.showNotification(error.message || 'An error occurred', 'error');
            if (errorCallback) errorCallback(error);
        })
        .finally(() => {
            if (submitBtn) {
                submitBtn.innerHTML = originalText;
                submitBtn.disabled = false;
            }
        });
    },

    // AJAX data loading with loading indicator
    loadData: function(url, container, loadingMessage = 'Loading...') {
        const containerEl = document.querySelector(container);
        if (!containerEl) return;
        
        // Show loading indicator
        containerEl.innerHTML = `
            <div class="text-center py-4">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">${loadingMessage}</span>
                </div>
                <div class="mt-2">${loadingMessage}</div>
            </div>
        `;
        
        fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            return response.text();
        })
        .then(html => {
            containerEl.innerHTML = html;
            // Re-initialize components in loaded content
            this.initTooltips();
            this.initConfirmations();
        })
        .catch(error => {
            containerEl.innerHTML = `
                <div class="alert alert-danger">
                    <i class="bi bi-exclamation-triangle"></i> Error loading data: ${error.message}
                </div>
            `;
        });
    },

    // Format date for display
    formatDate: function(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        return date.toLocaleDateString('en-GB'); // DD.MM.YYYY format
    },

    // Format currency
    formatCurrency: function(amount, currency = 'UAH') {
        return new Intl.NumberFormat('uk-UA', {
            style: 'currency',
            currency: currency
        }).format(amount);
    },

    // Debounce function for search inputs
    debounce: function(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    },

    // Data export functionality
    exportData: function(data, filename = 'export.json') {
        const blob = new Blob([JSON.stringify(data, null, 2)], {
            type: 'application/json'
        });
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);
    },

    // Print functionality
    printElement: function(elementId) {
        const element = document.getElementById(elementId);
        if (!element) return;
        
        const printWindow = window.open('', '_blank');
        printWindow.document.write(`
            <!DOCTYPE html>
            <html>
            <head>
                <title>Print - ECS</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
                <style>
                    @media print {
                        .no-print { display: none !important; }
                        body { background: white !important; }
                    }
                </style>
            </head>
            <body>
                ${element.outerHTML}
            </body>
            </html>
        `);
        printWindow.document.close();
        printWindow.print();
    }
};

// Utility functions for global use
window.showNotification = function(message, type, duration) {
    ECS.showNotification(message, type, duration);
};

window.loadData = function(url, container, loadingMessage) {
    ECS.loadData(url, container, loadingMessage);
};

window.submitForm = function(form, successCallback, errorCallback) {
    ECS.submitForm(form, successCallback, errorCallback);
};
