import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Button } from "@/components/ui/button"
import { FileText, GraduationCap, Users, Clock } from "lucide-react"
import Link from "next/link"

export default function TeacherDashboard() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold tracking-tight">Teacher Dashboard</h1>
        <p className="text-muted-foreground">
          Welcome back, Ms. Johnson! Here&apos;s an overview of your classes and assignments.
        </p>
      </div>
      <Tabs defaultValue="overview">
        <TabsList>
          <TabsTrigger value="overview">Overview</TabsTrigger>
          <TabsTrigger value="classes">Classes</TabsTrigger>
          <TabsTrigger value="recent">Recent Activity</TabsTrigger>
        </TabsList>
        <TabsContent value="overview" className="space-y-4">
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Active Assignments</CardTitle>
                <FileText className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">15</div>
                <p className="text-xs text-muted-foreground">4 need grading</p>
              </CardContent>
            </Card>
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Students</CardTitle>
                <Users className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">128</div>
                <p className="text-xs text-muted-foreground">Across 5 classes</p>
              </CardContent>
            </Card>
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Class Average</CardTitle>
                <GraduationCap className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">B+</div>
                <p className="text-xs text-muted-foreground">3.3 GPA average</p>
              </CardContent>
            </Card>
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Hours Taught</CardTitle>
                <Clock className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">87</div>
                <p className="text-xs text-muted-foreground">This semester</p>
              </CardContent>
            </Card>
          </div>
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-7">
            <Card className="col-span-4">
              <CardHeader>
                <CardTitle>Assignments Needing Grading</CardTitle>
                <CardDescription>Recently submitted assignments that need your attention</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {[
                    {
                      class: "Physics 101",
                      title: "Lab Report: Mechanics",
                      student: "Alex Johnson",
                      submitted: "Today, 10:30 AM",
                    },
                    {
                      class: "Physics 101",
                      title: "Lab Report: Mechanics",
                      student: "Jamie Smith",
                      submitted: "Yesterday, 3:45 PM",
                    },
                    {
                      class: "Physics 201",
                      title: "Quantum Mechanics Quiz",
                      student: "Taylor Wilson",
                      submitted: "Yesterday, 1:20 PM",
                    },
                    {
                      class: "Physics 101",
                      title: "Midterm Exam",
                      student: "Jordan Lee",
                      submitted: "Apr 18, 2025",
                    },
                  ].map((assignment, index) => (
                    <div key={index} className="flex items-center justify-between">
                      <div className="space-y-1">
                        <p className="text-sm font-medium leading-none">{assignment.title}</p>
                        <p className="text-sm text-muted-foreground">
                          {assignment.student} - {assignment.class}
                        </p>
                      </div>
                      <div className="flex items-center gap-4">
                        <div className="text-sm text-muted-foreground">{assignment.submitted}</div>
                        <Button size="sm">Grade</Button>
                      </div>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
            <Card className="col-span-3">
              <CardHeader>
                <CardTitle>Upcoming Classes</CardTitle>
                <CardDescription>Your schedule for the next few days</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {[
                    {
                      class: "Physics 101",
                      time: "Today, 2:00 PM - 3:30 PM",
                      room: "Science Hall 101",
                      students: 32,
                    },
                    {
                      class: "Physics 201",
                      time: "Today, 4:00 PM - 5:30 PM",
                      room: "Science Hall 203",
                      students: 24,
                    },
                    {
                      class: "Physics Lab",
                      time: "Tomorrow, 10:00 AM - 12:00 PM",
                      room: "Lab Building 3",
                      students: 16,
                    },
                    {
                      class: "Physics 101",
                      time: "Tomorrow, 2:00 PM - 3:30 PM",
                      room: "Science Hall 101",
                      students: 32,
                    },
                  ].map((schedule, index) => (
                    <div key={index} className="flex items-center justify-between">
                      <div className="space-y-1">
                        <p className="text-sm font-medium leading-none">{schedule.class}</p>
                        <p className="text-sm text-muted-foreground">{schedule.time}</p>
                      </div>
                      <div className="flex items-center gap-4">
                        <div className="text-sm text-muted-foreground">{schedule.room}</div>
                        <div className="flex items-center text-sm">
                          <Users className="mr-1 h-3 w-3" />
                          {schedule.students}
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          </div>
        </TabsContent>
        <TabsContent value="classes" className="space-y-4">
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {[
              {
                id: 1,
                name: "Physics 101",
                description: "Introduction to Physics",
                students: 32,
                level: "Level 1",
                group: "Group A",
              },
              {
                id: 2,
                name: "Physics 201",
                description: "Advanced Physics Concepts",
                students: 24,
                level: "Level 2",
                group: "Group B",
              },
              {
                id: 3,
                name: "Physics Lab",
                description: "Practical Physics Experiments",
                students: 16,
                level: "Level 1",
                group: "Group C",
              },
              {
                id: 4,
                name: "AP Physics",
                description: "College-level Physics",
                students: 18,
                level: "Level 3",
                group: "Group A",
              },
              {
                id: 5,
                name: "Physics Seminar",
                description: "Discussion-based Physics Course",
                students: 12,
                level: "Level 3",
                group: "Group B",
              },
            ].map((classItem) => (
              <Card key={classItem.id}>
                <CardHeader>
                  <CardTitle>{classItem.name}</CardTitle>
                  <CardDescription>{classItem.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="space-y-2">
                    <div className="flex justify-between">
                      <span className="text-sm text-muted-foreground">Students:</span>
                      <span className="text-sm">{classItem.students}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-sm text-muted-foreground">Level:</span>
                      <span className="text-sm">{classItem.level}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-sm text-muted-foreground">Group:</span>
                      <span className="text-sm">{classItem.group}</span>
                    </div>
                  </div>
                </CardContent>
                <div className="flex border-t p-4">
                  <Link href={`/dashboard/teacher/students?class=${classItem.id}`} className="w-full">
                    <Button variant="outline" className="w-full">
                      View Students
                    </Button>
                  </Link>
                </div>
              </Card>
            ))}
          </div>
        </TabsContent>
        <TabsContent value="recent" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>Recent Activity</CardTitle>
              <CardDescription>Your recent activity on the platform</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {[
                  {
                    action: "Created Assignment",
                    details: "Lab Report: Wave Properties for Physics 101",
                    timestamp: "Today, 9:30 AM",
                  },
                  {
                    action: "Graded Assignment",
                    details: "Midterm Exam for Alex Johnson",
                    timestamp: "Yesterday, 4:45 PM",
                  },
                  {
                    action: "Added Comment",
                    details: "On Jamie Smith's Lab Report",
                    timestamp: "Yesterday, 2:20 PM",
                  },
                  {
                    action: "Updated Class Schedule",
                    details: "Physics 201 moved to Science Hall 203",
                    timestamp: "Apr 18, 2025, 10:15 AM",
                  },
                  {
                    action: "Created Exam",
                    details: "Final Exam for Physics 101",
                    timestamp: "Apr 17, 2025, 3:30 PM",
                  },
                ].map((activity, index) => (
                  <div key={index} className="flex items-center justify-between">
                    <div className="space-y-1">
                      <p className="text-sm font-medium leading-none">{activity.action}</p>
                      <p className="text-sm text-muted-foreground">{activity.details}</p>
                    </div>
                    <div className="text-sm text-muted-foreground">{activity.timestamp}</div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  )
}
