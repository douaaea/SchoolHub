'use client';

import React, { useState } from 'react';

const AddStudent = () => {
  const [student, setStudent] = useState({
    email: '',
    password: '',
    firstname: '',
    lastname: '',
    groupId: '',
    levelId: ''
  });

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setStudent({
      ...student,
      [name]: value
    });
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    // Prepare data with correct types
    const payload = {
      ...student,
      groupId: Number(student.groupId),
      levelId: Number(student.levelId)
    };

    try {
      const response = await fetch('http://localhost:8080/api/students', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
      });

      if (response.ok) {
        alert('Student added successfully!');
        setStudent({
          email: '',
          password: '',
          firstname: '',
          lastname: '',
          groupId: '',
          levelId: ''
        });
      } else {
        alert('Failed to add student.');
      }
    } catch (error) {
      console.error('Error adding student:', error);
      alert('An error occurred.');
    }
  };

  return (
    <div className="add-student-form">
      <h2 className="text-2xl font-bold mb-4">Add New Student</h2>
      <form onSubmit={handleSubmit} className="space-y-4 max-w-md">
        <div>
          <label>Email:</label>
          <input
            type="email"
            name="email"
            value={student.email}
            onChange={handleInputChange}
            required
            className="w-full border p-2"
          />
        </div>
        <div>
          <label>Password:</label>
          <input
            type="password"
            name="password"
            value={student.password}
            onChange={handleInputChange}
            required
            className="w-full border p-2"
          />
        </div>
        <div>
          <label>First Name:</label>
          <input
            type="text"
            name="firstname"
            value={student.firstname}
            onChange={handleInputChange}
            required
            className="w-full border p-2"
          />
        </div>
        <div>
          <label>Last Name:</label>
          <input
            type="text"
            name="lastname"
            value={student.lastname}
            onChange={handleInputChange}
            required
            className="w-full border p-2"
          />
        </div>
        <div>
          <label>Group ID:</label>
          <input
            type="number"
            name="groupId"
            value={student.groupId}
            onChange={handleInputChange}
            className="w-full border p-2"
          />
        </div>
        <div>
          <label>Level ID:</label>
          <input
            type="number"
            name="levelId"
            value={student.levelId}
            onChange={handleInputChange}
            className="w-full border p-2"
          />
        </div>
        <button type="submit" className="bg-blue-600 text-white px-4 py-2 rounded">
          Add Student
        </button>
      </form>
    </div>
  );
};

export default AddStudent;
