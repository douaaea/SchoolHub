import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { CalendarDays, FileText, GraduationCap, Users } from "lucide-react"

export default function StudentDashboard() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold tracking-tight">Dashboard</h1>
        <p className="text-muted-foreground">Welcome back, Alex! Here&apos;s an overview of your academic progress.</p>
      </div>
      <Tabs defaultValue="overview">
        <TabsList>
          <TabsTrigger value="overview">Overview</TabsTrigger>
          <TabsTrigger value="upcoming">Upcoming</TabsTrigger>
          <TabsTrigger value="recent">Recent Activity</TabsTrigger>
        </TabsList>
        <TabsContent value="overview" className="space-y-4">
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Assignments</CardTitle>
                <FileText className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">12</div>
                <p className="text-xs text-muted-foreground">3 due this week</p>
              </CardContent>
            </Card>
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Current GPA</CardTitle>
                <GraduationCap className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">3.8</div>
                <p className="text-xs text-muted-foreground">+0.2 from last semester</p>
              </CardContent>
            </Card>
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Classes</CardTitle>
                <Users className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">6</div>
                <p className="text-xs text-muted-foreground">Level 2 - Group B</p>
              </CardContent>
            </Card>
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Attendance</CardTitle>
                <CalendarDays className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">98%</div>
                <p className="text-xs text-muted-foreground">2 absences this semester</p>
              </CardContent>
            </Card>
          </div>
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-7">
            <Card className="col-span-4">
              <CardHeader>
                <CardTitle>Recent Assignments</CardTitle>
                <CardDescription>Your most recent assignments and their status</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {[
                    {
                      subject: "Mathematics",
                      title: "Calculus Problem Set 3",
                      dueDate: "Apr 22, 2025",
                      status: "Submitted",
                    },
                    {
                      subject: "Physics",
                      title: "Lab Report: Wave Properties",
                      dueDate: "Apr 25, 2025",
                      status: "Not Started",
                    },
                    {
                      subject: "English Literature",
                      title: "Essay on Shakespeare",
                      dueDate: "Apr 28, 2025",
                      status: "In Progress",
                    },
                  ].map((assignment, index) => (
                    <div key={index} className="flex items-center justify-between">
                      <div className="space-y-1">
                        <p className="text-sm font-medium leading-none">{assignment.title}</p>
                        <p className="text-sm text-muted-foreground">{assignment.subject}</p>
                      </div>
                      <div className="flex items-center gap-4">
                        <div className="text-sm text-muted-foreground">{assignment.dueDate}</div>
                        <div
                          className={`text-sm ${
                            assignment.status === "Submitted"
                              ? "text-green-500"
                              : assignment.status === "In Progress"
                                ? "text-amber-500"
                                : "text-red-500"
                          }`}
                        >
                          {assignment.status}
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
            <Card className="col-span-3">
              <CardHeader>
                <CardTitle>Recent Grades</CardTitle>
                <CardDescription>Your most recent grades and feedback</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {[
                    {
                      subject: "Mathematics",
                      assignment: "Midterm Exam",
                      grade: "A",
                      score: "92/100",
                    },
                    {
                      subject: "Physics",
                      assignment: "Lab Report: Mechanics",
                      grade: "B+",
                      score: "88/100",
                    },
                    {
                      subject: "Computer Science",
                      assignment: "Programming Project",
                      grade: "A-",
                      score: "90/100",
                    },
                    {
                      subject: "History",
                      assignment: "Research Paper",
                      grade: "B",
                      score: "85/100",
                    },
                  ].map((grade, index) => (
                    <div key={index} className="flex items-center justify-between">
                      <div className="space-y-1">
                        <p className="text-sm font-medium leading-none">{grade.assignment}</p>
                        <p className="text-sm text-muted-foreground">{grade.subject}</p>
                      </div>
                      <div className="flex items-center gap-4">
                        <div className="text-sm text-muted-foreground">{grade.score}</div>
                        <div className="text-sm font-bold">{grade.grade}</div>
                      </div>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          </div>
        </TabsContent>
        <TabsContent value="upcoming" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>Upcoming Assignments</CardTitle>
              <CardDescription>Assignments due in the next two weeks</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {[
                  {
                    subject: "Physics",
                    title: "Lab Report: Wave Properties",
                    dueDate: "Apr 25, 2025",
                    status: "Not Started",
                  },
                  {
                    subject: "English Literature",
                    title: "Essay on Shakespeare",
                    dueDate: "Apr 28, 2025",
                    status: "In Progress",
                  },
                  {
                    subject: "Computer Science",
                    title: "Algorithm Implementation",
                    dueDate: "May 2, 2025",
                    status: "Not Started",
                  },
                  {
                    subject: "Chemistry",
                    title: "Periodic Table Quiz",
                    dueDate: "May 5, 2025",
                    status: "Not Started",
                  },
                ].map((assignment, index) => (
                  <div key={index} className="flex items-center justify-between">
                    <div className="space-y-1">
                      <p className="text-sm font-medium leading-none">{assignment.title}</p>
                      <p className="text-sm text-muted-foreground">{assignment.subject}</p>
                    </div>
                    <div className="flex items-center gap-4">
                      <div className="text-sm text-muted-foreground">{assignment.dueDate}</div>
                      <div
                        className={`text-sm ${
                          assignment.status === "Submitted"
                            ? "text-green-500"
                            : assignment.status === "In Progress"
                              ? "text-amber-500"
                              : "text-red-500"
                        }`}
                      >
                        {assignment.status}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <CardTitle>Upcoming Exams</CardTitle>
              <CardDescription>Exams scheduled in the next month</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {[
                  {
                    subject: "Mathematics",
                    title: "Final Exam",
                    date: "May 15, 2025",
                    location: "Room 101",
                  },
                  {
                    subject: "Physics",
                    title: "Final Exam",
                    date: "May 18, 2025",
                    location: "Science Hall",
                  },
                  {
                    subject: "English Literature",
                    title: "Final Paper Due",
                    date: "May 20, 2025",
                    location: "Online Submission",
                  },
                ].map((exam, index) => (
                  <div key={index} className="flex items-center justify-between">
                    <div className="space-y-1">
                      <p className="text-sm font-medium leading-none">{exam.title}</p>
                      <p className="text-sm text-muted-foreground">{exam.subject}</p>
                    </div>
                    <div className="flex items-center gap-4">
                      <div className="text-sm text-muted-foreground">{exam.date}</div>
                      <div className="text-sm">{exam.location}</div>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
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
                    action: "Submitted Assignment",
                    details: "Calculus Problem Set 3",
                    timestamp: "Today, 10:30 AM",
                  },
                  {
                    action: "Viewed Grade",
                    details: "Physics Lab Report: Mechanics",
                    timestamp: "Yesterday, 3:45 PM",
                  },
                  {
                    action: "Started Assignment",
                    details: "Essay on Shakespeare",
                    timestamp: "Yesterday, 1:20 PM",
                  },
                  {
                    action: "Downloaded Resource",
                    details: "Computer Science Lecture Notes",
                    timestamp: "Apr 18, 2025, 9:15 AM",
                  },
                  {
                    action: "Commented on Discussion",
                    details: "History Class Forum",
                    timestamp: "Apr 17, 2025, 2:30 PM",
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
