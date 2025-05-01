"use client"

type Subject = {
  id: number;
  name: string;
};

type Group = {
  id: number;
  name: string;
};

type Assignment = {
  id: number;
  title: string;
  description: string;
  delay: string; // Replaced dueDate with delay
  subject: Subject;
  group: Group;
  className: string;
};
interface AssignmentsState {
  active: Assignment[];
  past: Assignment[];
}

import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Textarea } from "@/components/ui/textarea"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Badge } from "@/components/ui/badge"
import { FileText, Plus, Edit } from "lucide-react"

export default function TeacherAssignments() {
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false)
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false)
  const [selectedAssignment, setSelectedAssignment] = useState<Assignment | null>(null)
  const [newAssignment, setNewAssignment] = useState({
    title: "",
    description: "",
    delay: "", // Correct field name for delay
    subject: "", // This can be a string or an object based on your structure
    group: "",   // Same as subject
    className:"",
  });
  
  
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [assignments, setAssignments] = useState<any>({ active: [], past: [] })
  const [loading, setLoading] = useState(true)

  // Fetch assignments from API
  const fetchAssignments = async () => {
    try {
      setLoading(true)
      const response = await fetch("/api/assignments")
      const data = await response.json()

      // Update assignments state with fetched data
      const activeAssignments = data.filter((assignment: any) => new Date(assignment.delay) > new Date()) // Changed from dueDate to delay
      const pastAssignments = data.filter((assignment: any) => new Date(assignment.delay) <= new Date()) // Changed from dueDate to delay

      setAssignments({
        active: activeAssignments,
        past: pastAssignments,
      })
    } catch (error) {
      console.error("Failed to fetch assignments", error)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchAssignments() // Fetch assignments on component mount
  }, [])

  const handleCreateAssignment = async () => {
    setIsSubmitting(true)
    try {
      const response = await fetch("/api/assignments", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(newAssignment),
      })

      if (response.ok) {
        setIsCreateDialogOpen(false)
        setNewAssignment({
          title: "",
          description: "",
          delay: "", // Correct field name for delay
          subject: "", // Reset subject
          group: "",// Reset group
          className:"",   
        })
        fetchAssignments() // Refresh assignments list
      }
    } catch (error) {
      console.error("Failed to create assignment", error)
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleEditAssignment = async () => {
    if (!selectedAssignment) return; // prevent crash
  
    setIsSubmitting(true)
    try {
      const response = await fetch(`/api/assignments/${selectedAssignment.id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(newAssignment),
      })
  
      if (response.ok) {
        setIsEditDialogOpen(false)
        setSelectedAssignment(null)
        fetchAssignments()
      }
    } catch (error) {
      console.error("Failed to edit assignment", error)
    } finally {
      setIsSubmitting(false)
    }
  }
  

   

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Assignments</h1>
          <p className="text-muted-foreground">Create, manage, and grade assignments for your classes.</p>
        </div>
        <Dialog open={isCreateDialogOpen} onOpenChange={setIsCreateDialogOpen}>
          <DialogTrigger asChild>
            <Button>
              <Plus className="mr-2 h-4 w-4" />
              Create Assignment
            </Button>
          </DialogTrigger>
          <DialogContent className="sm:max-w-[600px]">
            <DialogHeader>
              <DialogTitle>Create New Assignment</DialogTitle>
              <DialogDescription>Create a new assignment for your students.</DialogDescription>
            </DialogHeader>
            <div className="space-y-4 py-4">
              <div className="grid w-full gap-1.5">
                <Label htmlFor="title">Assignment Title</Label>
                <Input
                  id="title"
                  placeholder="Enter assignment title"
                  value={newAssignment.title}
                  onChange={(e) => setNewAssignment({ ...newAssignment, title: e.target.value })}
                />
              </div>
              <div className="grid w-full gap-1.5">
                <Label htmlFor="class">Class</Label>
                <Select
                value={newAssignment.className} 
                onValueChange={(value) => setNewAssignment({ ...newAssignment, className: value })}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Select a class" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="Mathematics 101">Mathematics 101</SelectItem>
                    <SelectItem value="Physics 101">Physics 101</SelectItem>
                    <SelectItem value="English Literature">English Literature</SelectItem>
                    <SelectItem value="Computer Science">Computer Science</SelectItem>
                    <SelectItem value="History">History</SelectItem>
                    <SelectItem value="Chemistry">Chemistry</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div className="grid w-full gap-1.5">
                <Label htmlFor="description">Description</Label>
                <Textarea
                  id="description"
                  placeholder="Enter assignment description"
                  value={newAssignment.description}
                  onChange={(e) => setNewAssignment({ ...newAssignment, description: e.target.value })}
                  className="min-h-[100px]"
                />
              </div>
              <div className="grid w-full gap-1.5">
                <Label htmlFor="delay">Delay (Due Date)</Label>
                <Input
                  id="delay"
                  type="date"
                  value={newAssignment.delay}
                  onChange={(e) => setNewAssignment({ ...newAssignment, delay: e.target.value })}
                />
              </div>
            </div>
            <DialogFooter>
              <Button variant="outline" onClick={() => setIsCreateDialogOpen(false)}>
                Cancel
              </Button>
              <Button onClick={handleCreateAssignment} disabled={isSubmitting}>
                {isSubmitting ? "Creating..." : "Create Assignment"}
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </div>
      <Tabs defaultValue="active">
        <TabsList>
          <TabsTrigger value="active">Active</TabsTrigger>
          <TabsTrigger value="past">Past</TabsTrigger>
        </TabsList>
        <TabsContent value="active" className="space-y-4">
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {assignments.active.map((assignment: Assignment) => (
              <Card key={assignment.id} className="overflow-hidden">
                <CardHeader className="pb-3">
                  <div className="flex items-center justify-between">
                    <Badge variant="outline">{assignment.subject.name}</Badge>
                    <span className="text-sm text-muted-foreground">Due: {assignment.delay}</span>
                  </div>
                  <CardTitle className="text-lg">{assignment.title}</CardTitle>
                  <CardDescription>
                    {assignment.description}
                  </CardDescription>
                </CardHeader>
                <CardContent className="text-sm">
                  <p>{assignment.description}</p>
                </CardContent>
                <CardFooter className="flex justify-between">
                  <Dialog>
                    <DialogTrigger asChild>
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() =>
                          setNewAssignment({
                            title: assignment.title,
                            description: assignment.description,
                            delay: assignment.delay,  // Using 'delay' instead of 'dueDate'
                            subject: assignment.subject.name,
                            group: assignment.group.name,
                            className: assignment.className
                          })
                        }
                      >
                        <Edit className="mr-2 h-4 w-4" />
                        Edit
                      </Button>
                    </DialogTrigger>
                    <DialogContent className="sm:max-w-[600px]">
                      <DialogHeader>
                        <DialogTitle>Edit Assignment</DialogTitle>
                        <DialogDescription>Make changes to the assignment details.</DialogDescription>
                      </DialogHeader>
                      <div className="space-y-4 py-4">
                        <div className="grid w-full gap-1.5">
                          <Label htmlFor="edit-title">Assignment Title</Label>
                          <Input
                            id="edit-title"
                            value={newAssignment.title}
                            onChange={(e) => setNewAssignment({ ...newAssignment, title: e.target.value })}
                          />
                        </div>
                        <div className="grid w-full gap-1.5">
                          <Label htmlFor="edit-class">Class</Label>
                          <Select
                            value={newAssignment.className}
                            onValueChange={(value) => setNewAssignment({ ...newAssignment, className: value })}
                          >
                            <SelectTrigger>
                              <SelectValue placeholder="Select a class" />
                            </SelectTrigger>
                            <SelectContent>
                              <SelectItem value="Mathematics 101">Mathematics 101</SelectItem>
                              <SelectItem value="Physics 101">Physics 101</SelectItem>
                              <SelectItem value="English Literature">English Literature</SelectItem>
                              <SelectItem value="Computer Science">Computer Science</SelectItem>
                              <SelectItem value="History">History</SelectItem>
                              <SelectItem value="Chemistry">Chemistry</SelectItem>
                            </SelectContent>
                          </Select>
                        </div>
                        <div className="grid w-full gap-1.5">
                          <Label htmlFor="edit-description">Description</Label>
                          <Textarea
                            id="edit-description"
                            value={newAssignment.description}
                            onChange={(e) => setNewAssignment({ ...newAssignment, description: e.target.value })}
                            className="min-h-[100px]"
                          />
                        </div>
                        <div className="grid w-full gap-1.5">
                          <Label htmlFor="edit-delay">Delay (Due Date)</Label>
                          <Input
                            id="edit-delay"
                            type="date"
                            value={newAssignment.delay}
                            onChange={(e) => setNewAssignment({ ...newAssignment, delay: e.target.value })}
                          />
                        </div>
                      </div>
                      <DialogFooter>
                        <Button variant="outline">Cancel</Button>
                        <Button onClick={handleEditAssignment}>Save Changes</Button>
                      </DialogFooter>
                    </DialogContent>
                  </Dialog>
                  <Button>
                    <FileText className="mr-2 h-4 w-4" />
                    View Submissions
                  </Button>
                </CardFooter>
              </Card>
            ))}
          </div>
        </TabsContent>
        <TabsContent value="past" className="space-y-4">
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
  {assignments.past.map((assignment: Assignment) => (
    <Card key={assignment.id} className="overflow-hidden">
      <CardHeader className="pb-3">
        <div className="flex items-center justify-between">
          <Badge variant="outline">{assignment.className}</Badge>
          {/* Remove or comment out the Avg and submissions properties */}
          {/* <span className="text-sm font-medium">Avg: {assignment.averageGrade}</span> */}
        </div>
        <CardTitle className="text-lg">{assignment.title}</CardTitle>
        <CardDescription>
          {/* Remove the submissions and totalStudents properties */}
          {/* Submissions: {assignment.submissions}/{assignment.totalStudents} */}
        </CardDescription>
      </CardHeader>
      <CardContent className="text-sm">
        <p>{assignment.description}</p>
        <p className="mt-2 text-xs text-muted-foreground">Due: {assignment.delay}</p>
      </CardContent>
      <CardFooter>
        <Button className="w-full">
          <FileText className="mr-2 h-4 w-4" />
          View Submissions
        </Button>
      </CardFooter>
    </Card>
  ))}
</div>

        </TabsContent>
      </Tabs>
    </div>
  )
}