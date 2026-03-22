package ca.humber.huynh.beans;


public class User {
	private Long userId; // Matches userId in DB
	private String email; // Matches email in DB
	private String encryptedPassword; // Matches encryptedPassword in DB
	private Boolean enabled; // Matches enabled in DB

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
}
