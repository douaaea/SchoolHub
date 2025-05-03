'use client';

import React, { useEffect, useState } from 'react';

interface Teacher {
  id: number;
  email: string;
  firstname: string;
  lastname: string;
}

const TeachersPage = () => {
  const [teachers, setTeachers] = useState<Teacher[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchTeachers = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/teachers');
        const data = await response.json();
        setTeachers(data);
      } catch (error) {
        console.error('Error fetching teachers:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchTeachers();
  }, []);

  return (
    <div>
      <h1 className="text-2xl font-bold mb-4">All Teachers</h1>

      {loading ? (
        <p>Loading...</p>
      ) : teachers.length === 0 ? (
        <p>No teachers found.</p>
      ) : (
        <table className="min-w-full border border-gray-300">
          <thead>
            <tr className="bg-gray-100">
              <th className="border px-4 py-2">ID</th>
              <th className="border px-4 py-2">First Name</th>
              <th className="border px-4 py-2">Last Name</th>
              <th className="border px-4 py-2">Email</th>
            </tr>
          </thead>
          <tbody>
            {teachers.map((teacher) => (
              <tr key={teacher.id}>
                <td className="border px-4 py-2">{teacher.id}</td>
                <td className="border px-4 py-2">{teacher.firstname}</td>
                <td className="border px-4 py-2">{teacher.lastname}</td>
                <td className="border px-4 py-2">{teacher.email}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default TeachersPage;
