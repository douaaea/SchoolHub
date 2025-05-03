'use client';

import React, { useState } from 'react';

const AddTeacher = () => {
  const [teacher, setTeacher] = useState({
    email: '',
    password: '',
    firstname: '',
    lastname: ''
  });

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setTeacher({
      ...teacher,
      [name]: value
    });
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    try {
      const response = await fetch('http://localhost:8080/api/teachers', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(teacher)
      });

      if (response.ok) {
        alert('Teacher added successfully!');
        setTeacher({
          email: '',
          password: '',
          firstname: '',
          lastname: ''
        });
      } else {
        alert('Failed to add teacher.');
      }
    } catch (error) {
      console.error('Error adding teacher:', error);
      alert('An error occurred.');
    }
  };

  return (
    <div className="add-teacher-form">
      <h2>Add New Teacher</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label>Email:</label>
          <input
            type="email"
            name="email"
            value={teacher.email}
            onChange={handleInputChange}
            required
          />
        </div>
        <div>
          <label>Password:</label>
          <input
            type="password"
            name="password"
            value={teacher.password}
            onChange={handleInputChange}
            required
          />
        </div>
        <div>
          <label>First Name:</label>
          <input
            type="text"
            name="firstname"
            value={teacher.firstname}
            onChange={handleInputChange}
            required
          />
        </div>
        <div>
          <label>Last Name:</label>
          <input
            type="text"
            name="lastname"
            value={teacher.lastname}
            onChange={handleInputChange}
            required
          />
        </div>
        <button type="submit">Add Teacher</button>
      </form>
    </div>
  );
};

export default AddTeacher;
