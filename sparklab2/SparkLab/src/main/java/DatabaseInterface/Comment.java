package DatabaseInterface;

import java.util.Objects;

public class Comment {
	private String id;
	private String body;
	private String userId;
	private User user;

	public Comment(String id, String body, String userId) {
		this.id = id;
		this.body = body;
		this.userId = userId;
		this.user = null;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}


	public void setUser(User u) {
		this.user= user;
	}

	public User getUser() {
		return this.user;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Comment other = (Comment) obj;
		return Objects.equals(id, other.id) &&
				Objects.equals(body, other.body) &&
				Objects.equals(userId, other.userId) ;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, body, userId);
	}

	@Override
	public String toString() {
		return super.toString();
	}

	public void print() {
		System.out.println(body);
	}
	
	
	
	
}