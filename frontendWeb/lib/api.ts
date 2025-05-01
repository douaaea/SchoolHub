// src/lib/api.ts
export async function loginUser(email: string, password: string) {
    const res = await fetch("http://localhost:8080/api/users/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ email, password })
    });
  
    if (!res.ok) {
      const errorMsg = await res.text();
      throw new Error(errorMsg || "Login failed");
    }
  
    return res.json(); // returns user data or token (if you implement it)
  }
  