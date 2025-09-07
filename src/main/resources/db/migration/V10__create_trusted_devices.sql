-- Create trusted_devices table for device remembering functionality
CREATE TABLE trusted_devices (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    device_identifier VARCHAR(255) NOT NULL,
    device_name VARCHAR(255),
    user_agent VARCHAR(1000),
    ip_address VARCHAR(45),
    trusted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    last_used TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    
    CONSTRAINT fk_trusted_devices_user 
        FOREIGN KEY (user_id) REFERENCES users(id) 
        ON DELETE CASCADE,
    
    INDEX idx_trusted_devices_user_device (user_id, device_identifier),
    INDEX idx_trusted_devices_expires (expires_at),
    INDEX idx_trusted_devices_active (active)
);
