"use client"

import { useEffect, useState } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle, CardDescription, CardFooter } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"
import { toast } from "@/components/ui/use-toast"

type Grade = {
  id: number
  score: number
  studentId: number
  subjectId: number
  assignmentId: number
  studentName: string
  subjectName: string
  assignmentName: string
}

type Student = { id: number; firstname: string; lastname: string }
type Subject = { id: number; name: string }
type Assignment = {
  id: number
  title: string
  description: string
  delay: string
  subjectId: number
  groupId: number
  programId: number
}

const BACKEND_URL = "http://localhost:8080/api"

export default function AddGradeForm() {
  const [students, setStudents] = useState<Student[]>([])
  const [subjects, setSubjects] = useState<Subject[]>([])
  const [assignments, setAssignments] = useState<Assignment[]>([])
  const [grades, setGrades] = useState<Grade[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const [newGrade, setNewGrade] = useState<Grade>({
    id: 0,
    score: 0,
    studentId: 0,
    subjectId: 0,
    assignmentId: 0,
    studentName: "",
    subjectName: "",
    assignmentName: "",
  })

  // Load reference data (students, subjects, assignments)
  const loadReferenceData = async () => {
    try {
      console.log(`[DEBUG] Fetching reference data from ${BACKEND_URL}`);
      const [studentsRes, subjectsRes, assignmentsRes] = await Promise.all([
        fetch(`${BACKEND_URL}/students`),
        fetch(`${BACKEND_URL}/subjects`),
        fetch(`${BACKEND_URL}/assignments/all`),
      ])

      console.log(`[DEBUG] Students fetch status: ${studentsRes.status} ${studentsRes.statusText}`);
      console.log(`[DEBUG] Subjects fetch status: ${subjectsRes.status} ${subjectsRes.statusText}`);
      console.log(`[DEBUG] Assignments fetch status: ${assignmentsRes.status} ${assignmentsRes.statusText}`);

      if (!studentsRes.ok) {
        const errorText = await studentsRes.text();
        console.error(`[DEBUG] Students fetch error response: ${errorText}`);
        throw new Error("Failed to fetch students");
      }
      if (!subjectsRes.ok) {
        const errorText = await subjectsRes.text();
        console.error(`[DEBUG] Subjects fetch error response: ${errorText}`);
        throw new Error("Failed to fetch subjects");
      }
      if (!assignmentsRes.ok) {
        const errorText = await assignmentsRes.text();
        console.error(`[DEBUG] Assignments fetch error response: ${errorText}`);
        throw new Error("Failed to fetch assignments");
      }

      const [studentsData, subjectsData, assignmentsData] = await Promise.all([
        studentsRes.json(),
        subjectsRes.json(),
        assignmentsRes.json(),
      ])

      console.log(`[DEBUG] Students response:`, studentsData);
      console.log(`[DEBUG] Subjects response:`, subjectsData);
      console.log(`[DEBUG] Assignments response:`, assignmentsData);

      setStudents(Array.isArray(studentsData) ? studentsData : []);
      setSubjects(Array.isArray(subjectsData) ? subjectsData : []);
      setAssignments(Array.isArray(assignmentsData) ? assignmentsData.map((a: any) => ({
        id: a.id,
        title: a.title,
        description: a.description,
        delay: a.delay,
        subjectId: a.subjectId || a.subject?.id || 0,
        groupId: a.groupId || a.group?.id || 0,
        programId: a.programId || a.program?.id || 0,
      })) : []);

      return { studentsData, subjectsData, assignmentsData };
    } catch (error) {
      console.error(`[DEBUG] Error loading reference data:`, error);
      setError(error instanceof Error ? error.message : "Failed to load reference data");
      return { studentsData: [], subjectsData: [], assignmentsData: [] };
    }
  }

  // Load and enrich grades with names
  const loadAndEnrichGrades = async (referenceData: {
    studentsData: Student[]
    subjectsData: Subject[]
    assignmentsData: Assignment[]
  }) => {
    try {
      console.log(`[DEBUG] Fetching grades from ${BACKEND_URL}/grades`);
      const gradesRes = await fetch(`${BACKEND_URL}/grades`);
      console.log(`[DEBUG] Grades fetch status: ${gradesRes.status} ${gradesRes.statusText}`);

      if (!gradesRes.ok) {
        const errorText = await gradesRes.text();
        console.error(`[DEBUG] Grades fetch error response: ${errorText}`);
        throw new Error("Failed to fetch grades");
      }

      const gradesData = await gradesRes.json();
      console.log(`[DEBUG] Grades response:`, gradesData);

      const enrichedGrades = Array.isArray(gradesData) ? gradesData.map((grade: any) => {
        const student = referenceData.studentsData.find(s => s.id === (grade.studentId || grade.student?.id));
        const subject = referenceData.subjectsData.find(s => s.id === (grade.subjectId || grade.subject?.id));
        const assignment = referenceData.assignmentsData.find(a => a.id === (grade.assignmentId || grade.assignment?.id));

        console.log(`[DEBUG] Enriching grade id ${grade.id}: studentId=${grade.studentId || grade.student?.id}, subjectId=${grade.subjectId || grade.subject?.id}, assignmentId=${grade.assignmentId || grade.assignment?.id}`);

        return {
          id: grade.id,
          score: grade.score,
          studentId: grade.studentId || grade.student?.id || 0,
          subjectId: grade.subjectId || grade.subject?.id || 0,
          assignmentId: grade.assignmentId || grade.assignment?.id || 0,
          studentName: student ? `${student.firstname} ${student.lastname}` : "Unknown Student",
          subjectName: subject?.name || "Unknown Subject",
          assignmentName: assignment?.title || "Unknown Assignment",
        };
      }) : [];

      setGrades(enrichedGrades);
      if (enrichedGrades.length === 0) {
        console.log(`[DEBUG] No grades found`);
        toast({
          title: "No Grades",
          description: "No existing grades found.",
          variant: "default",
        });
      }
    } catch (error) {
      console.error(`[DEBUG] Error loading grades:`, error);
      setError(error instanceof Error ? error.message : "Failed to load grades");
      setGrades([]);
      toast({
        title: "Error",
        description: error instanceof Error ? error.message : "Failed to load grades",
        variant: "destructive",
      });
    }
  }

  // Load data in sequence
  useEffect(() => {
    const fetchData = async () => {
      setIsLoading(true);
      setError(null);
      try {
        const referenceData = await loadReferenceData();
        await loadAndEnrichGrades(referenceData);
      } catch (error) {
        console.error(`[DEBUG] Error in fetch sequence:`, error);
      } finally {
        setIsLoading(false);
        console.log(`[DEBUG] Fetch completed, isLoading: false`);
      }
    };

    fetchData();
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const gradeToSend = {
      score: newGrade.score,
      studentId: newGrade.studentId,
      subjectId: newGrade.subjectId,
      assignmentId: newGrade.assignmentId,
    };

    try {
      console.log(`[DEBUG] Submitting grade:`, gradeToSend);
      const res = await fetch(`${BACKEND_URL}/grades${newGrade.id ? `/${newGrade.id}` : ''}`, {
        method: newGrade.id ? "PUT" : "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(gradeToSend),
      });

      console.log(`[DEBUG] Grade submission status: ${res.status} ${res.statusText}`);
      if (!res.ok) {
        const responseText = await res.text();
        let errorMessage = "Failed to save grade";
        try {
          const errorData = JSON.parse(responseText);
          errorMessage = errorData.message || errorData.error || errorMessage;
          console.error(`[DEBUG] Grade submission error response:`, errorData);
        } catch {
          console.error(`[DEBUG] Failed to parse error response as JSON:`, responseText);
          errorMessage = responseText || errorMessage;
        }
        throw new Error(errorMessage);
      }

      const savedGrade = await res.json();
      console.log(`[DEBUG] Saved grade:`, savedGrade);

      // Normalize IDs from response
      const student = students.find(s => s.id === (savedGrade.studentId || savedGrade.student?.id || newGrade.studentId));
      const subject = subjects.find(s => s.id === (savedGrade.subjectId || savedGrade.subject?.id || newGrade.subjectId));
      const assignment = assignments.find(a => a.id === (savedGrade.assignmentId || savedGrade.assignment?.id || newGrade.assignmentId));

      const updatedGrade = {
        id: savedGrade.id,
        score: savedGrade.score,
        studentId: savedGrade.studentId || savedGrade.student?.id || newGrade.studentId || 0,
        subjectId: savedGrade.subjectId || savedGrade.subject?.id || newGrade.subjectId || 0,
        assignmentId: savedGrade.assignmentId || savedGrade.assignment?.id || newGrade.assignmentId || 0,
        studentName: student ? `${student.firstname} ${student.lastname}` : "Unknown Student",
        subjectName: subject?.name || "Unknown Subject",
        assignmentName: assignment?.title || "Unknown Assignment",
      };
      console.log(`[DEBUG] Normalized grade:`, updatedGrade);

      // Refresh grades
      const referenceData = { studentsData: students, subjectsData: subjects, assignmentsData: assignments };
      await loadAndEnrichGrades(referenceData);

      // Reset form
      setNewGrade({
        id: 0,
        score: 0,
        studentId: 0,
        subjectId: 0,
        assignmentId: 0,
        studentName: "",
        subjectName: "",
        assignmentName: "",
      });

      toast({
        title: "Success",
        description: newGrade.id ? "Grade updated successfully!" : "Grade created successfully!",
      });
    } catch (error) {
      console.error(`[DEBUG] Error submitting grade:`, error);
      toast({
        title: "Error",
        description: error instanceof Error ? error.message : "Failed to save grade",
        variant: "destructive",
      });
    }
  };

  const handleEdit = (grade: Grade) => {
    setNewGrade(grade);
  };

  const handleDelete = async (id: number) => {
    try {
      console.log(`[DEBUG] Deleting grade id: ${id}`);
      const res = await fetch(`${BACKEND_URL}/grades/${id}`, { method: "DELETE" });

      console.log(`[DEBUG] Delete grade status: ${res.status} ${res.statusText}`);
      if (!res.ok) {
        const responseText = await res.text();
        let errorMessage = "Failed to delete grade";
        try {
          const errorData = JSON.parse(responseText);
          errorMessage = errorData.message || errorData.error || errorMessage;
          console.error(`[DEBUG] Delete grade error response:`, errorData);
        } catch {
          console.error(`[DEBUG] Failed to parse delete error response as JSON:`, responseText);
          errorMessage = responseText || errorMessage;
        }
        throw new Error(errorMessage);
      }

      // Refresh grades
      const referenceData = { studentsData: students, subjectsData: subjects, assignmentsData: assignments };
      await loadAndEnrichGrades(referenceData);

      toast({
        title: "Success",
        description: "Grade deleted successfully!",
      });
    } catch (error) {
      console.error(`[DEBUG] Error deleting grade:`, error);
      toast({
        title: "Error",
        description: error instanceof Error ? error.message : "Failed to delete grade",
        variant: "destructive",
      });
    }
  };

  return (
    <div className="space-y-6" style={{ padding: "20px" }}>
      <div>
        <h1 className="text-3xl font-bold tracking-tight">Manage Grades</h1>
        <p className="text-muted-foreground">Add, edit, or delete grades for student assignments.</p>
      </div>

      {isLoading && <p>Loading grades...</p>}
      {error && <p style={{ color: "red" }}>{error}</p>}

      <Card>
        <CardHeader>
          <CardTitle>{newGrade.id ? "Edit Grade" : "Add Grade"}</CardTitle>
          <CardDescription>Fill in the details to {newGrade.id ? "update" : "create"} a grade.</CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="score">Score</Label>
              <Input
                id="score"
                type="number"
                min="0"
                max="100"
                placeholder="Enter score (0-100)"
                value={newGrade.score}
                onChange={(e) => setNewGrade({ ...newGrade, score: Number(e.target.value) })}
                required
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="studentId">Student</Label>
              <select
                id="studentId"
                value={newGrade.studentId}
                onChange={(e) => setNewGrade({ ...newGrade, studentId: Number(e.target.value) })}
                required
                className="w-full border rounded-md p-2"
              >
                <option value={0} disabled>Select Student</option>
                {students.map((student) => (
                  <option key={student.id} value={student.id}>
                    {student.firstname} {student.lastname}
                  </option>
                ))}
              </select>
            </div>
            <div className="space-y-2">
              <Label htmlFor="subjectId">Subject</Label>
              <select
                id="subjectId"
                value={newGrade.subjectId}
                onChange={(e) => setNewGrade({ ...newGrade, subjectId: Number(e.target.value) })}
                required
                className="w-full border rounded-md p-2"
              >
                <option value={0} disabled>Select Subject</option>
                {subjects.map((subject) => (
                  <option key={subject.id} value={subject.id}>
                    {subject.name}
                  </option>
                ))}
              </select>
            </div>
            <div className="space-y-2">
              <Label htmlFor="assignmentId">Assignment</Label>
              <select
                id="assignmentId"
                value={newGrade.assignmentId}
                onChange={(e) => setNewGrade({ ...newGrade, assignmentId: Number(e.target.value) })}
                required
                className="w-full border rounded-md p-2"
              >
                <option value={0} disabled>Select Assignment</option>
                {assignments.length === 0 ? (
                  <option disabled>No assignments available</option>
                ) : (
                  assignments.map((assignment) => (
                    <option key={assignment.id} value={assignment.id}>
                      {assignment.title}
                    </option>
                  ))
                )}
              </select>
            </div>
            <div className="space-x-2">
              <Button type="submit">{newGrade.id ? "Update Grade" : "Add Grade"}</Button>
              {newGrade.id !== 0 && (
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => setNewGrade({
                    id: 0,
                    score: 0,
                    studentId: 0,
                    subjectId: 0,
                    assignmentId: 0,
                    studentName: "",
                    subjectName: "",
                    assignmentName: "",
                  })}
                >
                  Cancel Edit
                </Button>
              )}
            </div>
          </form>
        </CardContent>
      </Card>

      {grades.length === 0 && !isLoading && !error ? (
        <p className="text-muted-foreground">No grades found.</p>
      ) : (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          {grades.map((grade) => (
            <Card key={grade.id} className="overflow-hidden">
              <CardHeader className="pb-3">
                <CardTitle className="text-lg">{grade.assignmentName}</CardTitle>
                <CardDescription>{grade.studentName}</CardDescription>
              </CardHeader>
              <CardContent className="text-sm">
                <p><strong>Score:</strong> {grade.score}</p>
                <p><strong>Subject:</strong> {grade.subjectName}</p>
              </CardContent>
              <CardFooter className="space-x-2">
                <Button onClick={() => handleEdit(grade)}>Edit</Button>
                <Dialog>
                  <DialogTrigger asChild>
                    <Button variant="destructive">Delete</Button>
                  </DialogTrigger>
                  <DialogContent>
                    <DialogHeader>
                      <DialogTitle>Confirm Deletion</DialogTitle>
                      <DialogDescription>
                        Are you sure you want to delete the grade for "{grade.studentName}" on "{grade.assignmentName}"? This action cannot be undone.
                      </DialogDescription>
                    </DialogHeader>
                    <DialogFooter>
                      <Button variant="outline" onClick={() => {}}>
                        Cancel
                      </Button>
                      <Button variant="destructive" onClick={() => handleDelete(grade.id)}>
                        Delete
                      </Button>
                    </DialogFooter>
                  </DialogContent>
                </Dialog>
              </CardFooter>
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}