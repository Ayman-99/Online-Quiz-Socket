package socketproject;

public class User {
	
	private String username;
	private String password;
	private String score;
	
	public User(String username, String password, String score) {
		this.username = username;
		this.password = password;
		this.score = score;
	}
	
	public void addPoint() {
		this.score = String.valueOf(Integer.parseInt(score) + 1);
	}
	public void updateScore(String score) {
		this.score = score;
	}
	
	public String getScore() {
		return this.score;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
