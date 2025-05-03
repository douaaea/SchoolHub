'use client';

import React, { useEffect, useState } from 'react';

interface Level {
  id: number;
  name: string;
}

interface Subject {
  id: number;
  name: string;
  levelName: string; // Add levelName to the Subject interface
}

const SubjectsPage = () => {
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchSubjects = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/subjects');
        const data = await response.json();
        setSubjects(data);
      } catch (error) {
        console.error('Error fetching subjects:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchSubjects();
  }, []);

  return (
    <div>
      <h1 className="text-2xl font-bold mb-4">All Subjects</h1>

      {loading ? (
        <p>Loading...</p>
      ) : subjects.length === 0 ? (
        <p>No subjects found.</p>
      ) : (
        <table className="min-w-full border border-gray-300">
          <thead>
            <tr className="bg-gray-100">
              <th className="border px-4 py-2">ID</th>
              <th className="border px-4 py-2">Name</th>
              <th className="border px-4 py-2">Level</th>
            </tr>
          </thead>
          <tbody>
            {subjects.map((subject) => (
              <tr key={subject.id}>
                <td className="border px-4 py-2">{subject.id}</td>
                <td className="border px-4 py-2">{subject.name}</td>
                <td className="border px-4 py-2">{subject.levelName ?? 'N/A'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default SubjectsPage;
