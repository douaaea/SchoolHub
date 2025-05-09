"use client"

import { useEffect, useState } from 'react'

type Grade = {
  id: number
  score: number
  subject: {
    id: number
    name: string
  } | null
  assignment: {
    id: number
    title: string
  } | null
  createdAt: string
}

export default function StudentGradesPage() {
  const [grades, setGrades] = useState<Grade[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [filter, setFilter] = useState('all')

  useEffect(() => {
    const fetchGrades = async () => {
      try {
        const studentId = 1 // Replace with actual logged-in student ID
        const response = await fetch(`http://localhost:8080/api/grades?studentId=${studentId}`)
        
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`)
        
        const data = await response.json()
        setGrades(data.map((grade: any) => ({
          ...grade,
          subject: grade.subject || { id: 0, name: 'Unknown Subject' },
          assignment: grade.assignment || { id: 0, title: 'Unknown Assignment' }
        })))
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Failed to load grades')
      } finally {
        setLoading(false)
      }
    }

    fetchGrades()
  }, [])

  // Filter grades based on selection
  const filteredGrades = grades.filter(grade => {
    if (filter === 'passed') return grade.score >= 50
    if (filter === 'failed') return grade.score < 50
    return true
  })

  // Calculate average score (only for grades with valid scores)
  const validGrades = grades.filter(grade => !isNaN(grade.score))
  const averageScore = validGrades.length > 0 
    ? (validGrades.reduce((sum, grade) => sum + grade.score, 0) / validGrades.length).toFixed(2)
    : 'N/A'

  if (loading) {
    return <div className="text-center py-10">Loading grades...</div>
  }

  if (error) {
    return (
      <div className="max-w-4xl mx-auto mt-10 p-6 bg-white rounded-lg shadow-md">
        <h1 className="text-2xl font-bold mb-4">My Grades</h1>
        <div className="text-red-500">{error}</div>
        <button 
          onClick={() => window.location.reload()}
          className="mt-4 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
        >
          Retry
        </button>
      </div>
    )
  }

  return (
    <div className="max-w-4xl mx-auto mt-10 p-6 bg-white rounded-lg shadow-md">
      <h1 className="text-2xl font-bold mb-6">My Grades</h1>
      
      {/* Stats and filters */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-6 gap-4">
        <div className="bg-blue-50 p-4 rounded-lg">
          <h3 className="font-medium text-blue-800">Average Score</h3>
          <p className="text-2xl font-bold text-blue-600">{averageScore}</p>
        </div>
        
        <div className="flex items-center gap-3">
          <span className="text-sm font-medium">Filter:</span>
          <select
            value={filter}
            onChange={(e) => setFilter(e.target.value)}
            className="px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="all">All Grades</option>
            <option value="passed">Passed Only</option>
            <option value="failed">Failed Only</option>
          </select>
        </div>
      </div>

      {/* Grades table */}
      {filteredGrades.length === 0 ? (
        <div className="text-center py-10 text-gray-500">
          No grades found
        </div>
      ) : (
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Subject</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Assignment</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Score</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredGrades.map((grade) => (
                <tr key={grade.id}>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="font-medium text-gray-900">
                      {grade.subject?.name || 'Unknown Subject'}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-gray-900">
                      {grade.assignment?.title || 'Unknown Assignment'}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className={`font-bold ${grade.score >= 50 ? 'text-green-600' : 'text-red-600'}`}>
                      {grade.score}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full 
                      ${grade.score >= 50 ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
                      {grade.score >= 50 ? 'Passed' : 'Failed'}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {new Date(grade.createdAt).toLocaleDateString()}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}