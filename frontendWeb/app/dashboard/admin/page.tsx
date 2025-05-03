import { Badge } from "@/components/ui/badge"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Button } from "@/components/ui/button"
import { BookOpen, GraduationCap, Users, Settings, Bell, School } from "lucide-react"
import Link from "next/link"

export default function AdminDashboard() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold tracking-tight">Admin Dashboard</h1>
        <p className="text-muted-foreground">
          Welcome back, Principal Williams! Here&apos;s an overview of your school.
        </p>
      </div>
      <Tabs defaultValue="overview">
        <TabsList>
          <TabsTrigger value="overview">Overview</TabsTrigger>
          <TabsTrigger value="users">Users</TabsTrigger>
          <TabsTrigger value="notifications">Notifications</TabsTrigger>
        </TabsList>
        <TabsContent value="overview" className="space-y-4">
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Total Students</CardTitle>
                <Users className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">1,248</div>
                <p className="text-xs text-muted-foreground">+12% from last year</p>
              </CardContent>
            </Card>
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Teachers</CardTitle>
                <BookOpen className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">64</div>
                <p className="text-xs text-muted-foreground">+4 new this semester</p>
              </CardContent>
            </Card>
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Classes</CardTitle>
                <School className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">86</div>
                <p className="text-xs text-muted-foreground">Across 12 subjects</p>
              </CardContent>
            </Card>
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Average GPA</CardTitle>
                <GraduationCap className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">3.4</div>
                <p className="text-xs text-muted-foreground">+0.2 from last year</p>
              </CardContent>
            </Card>
          </div>
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-7">
            <Card className="col-span-4">
              <CardHeader>
                <CardTitle>School Performance</CardTitle>
                <CardDescription>Academic performance across different subjects</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {[
                    {
                      subject: "Mathematics",
                      averageGrade: "B+",
                      passingRate: "92%",
                      trend: "+3%",
                    },
                    {
                      subject: "Science",
                      averageGrade: "A-",
                      passingRate: "95%",
                      trend: "+5%",
                    },
                    {
                      subject: "English",
                      averageGrade: "B",
                      passingRate: "89%",
                      trend: "+1%",
                    },
                    {
                      subject: "History",
                      averageGrade: "B-",
                      passingRate: "87%",
                      trend: "-2%",
                    },
                    {
                      subject: "Computer Science",
                      averageGrade: "A",
                      passingRate: "96%",
                      trend: "+7%",
                    },
                  ].map((subject, index) => (
                    <div key={index} className="flex items-center justify-between">
                      <div className="space-y-1">
                        <p className="text-sm font-medium leading-none">{subject.subject}</p>
                        <p className="text-sm text-muted-foreground">Avg. Grade: {subject.averageGrade}</p>
                      </div>
                      <div className="flex items-center gap-4">
                        <div className="text-sm">Passing: {subject.passingRate}</div>
                        <div className={`text-sm ${subject.trend.startsWith("+") ? "text-green-500" : "text-red-500"}`}>
                          {subject.trend}
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
            <Card className="col-span-3">
              <CardHeader>
                <CardTitle>Recent System Activity</CardTitle>
                <CardDescription>Latest administrative actions</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {[
                    {
                      action: "New Teacher Added",
                      details: "Dr. Sarah Johnson - Physics",
                      timestamp: "Today, 10:30 AM",
                    },
                    {
                      action: "Class Schedule Updated",
                      details: "Spring Semester 2025",
                      timestamp: "Yesterday, 3:45 PM",
                    },
                    {
                      action: "System Maintenance",
                      details: "Database optimization completed",
                      timestamp: "Yesterday, 1:20 AM",
                    },
                    {
                      action: "New Student Enrolled",
                      details: "15 new students added to system",
                      timestamp: "Apr 18, 2025",
                    },
                    {
                      action: "Grading Period Closed",
                      details: "Winter Quarter grades finalized",
                      timestamp: "Apr 15, 2025",
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
          </div>
        </TabsContent>
        <TabsContent value="users" className="space-y-4">
          <div className="grid gap-4 md:grid-cols-2">
            <Card>
              <CardHeader>
                <CardTitle>Teachers</CardTitle>
                <CardDescription>Manage your teaching staff</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="space-y-2">
                  {[
                    {
                      name: "Dr. Sarah Johnson",
                      department: "Physics",
                      classes: 5,
                      students: 128,
                    },
                    {
                      name: "Prof. Michael Chen",
                      department: "Mathematics",
                      classes: 4,
                      students: 112,
                    },
                    {
                      name: "Ms. Emily Rodriguez",
                      department: "English",
                      classes: 6,
                      students: 156,
                    },
                  ].map((teacher, index) => (
                    <div key={index} className="flex items-center justify-between">
                      <div className="space-y-1">
                        <p className="text-sm font-medium leading-none">{teacher.name}</p>
                        <p className="text-sm text-muted-foreground">{teacher.department}</p>
                      </div>
                      <div className="flex items-center gap-4">
                        <div className="text-sm text-muted-foreground">
                          {teacher.classes} classes, {teacher.students} students
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
                <Link href="/dashboard/admin/teachers">
                  <Button variant="outline" className="w-full">
                    View All Teachers
                  </Button>
                </Link>
              </CardContent>
            </Card>
            <Card>
              <CardHeader>
                <CardTitle>Students</CardTitle>
                <CardDescription>Manage your student body</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="space-y-2">
                  {[
                    {
                      name: "Alex Johnson",
                      level: "Level 2",
                      group: "Group B",
                      gpa: "3.8",
                    },
                    {
                      name: "Jamie Smith",
                      level: "Level 1",
                      group: "Group A",
                      gpa: "3.5",
                    },
                    {
                      name: "Taylor Wilson",
                      level: "Level 3",
                      group: "Group C",
                      gpa: "4.0",
                    },
                  ].map((student, index) => (
                    <div key={index} className="flex items-center justify-between">
                      <div className="space-y-1">
                        <p className="text-sm font-medium leading-none">{student.name}</p>
                        <p className="text-sm text-muted-foreground">
                          {student.level} - {student.group}
                        </p>
                      </div>
                      <div className="flex items-center gap-4">
                        <div className="text-sm text-muted-foreground">GPA: {student.gpa}</div>
                      </div>
                    </div>
                  ))}
                </div>
                <Link href="/dashboard/admin/students">
                  <Button variant="outline" className="w-full">
                    View All Students
                  </Button>
                </Link>
              </CardContent>
            </Card>
          </div>
          <Card>
            <CardHeader>
              <CardTitle>User Management</CardTitle>
              <CardDescription>Quick actions for user management</CardDescription>
            </CardHeader>
            <CardContent className="flex flex-wrap gap-4">
              <Link href="/dashboard/admin/teachers/new">
                <Button>
                  <BookOpen className="mr-2 h-4 w-4" />
                  Manage Teachers 
                </Button>
              </Link>
              <Link href="/dashboard/admin/students/new">
                <Button>
                  <Users className="mr-2 h-4 w-4" />
                  Manage Students
                </Button>
              </Link>
              <Link href="/dashboard/admin/classes">
                <Button variant="outline">
                  <School className="mr-2 h-4 w-4" />
                  Manage Classes
                </Button>
              </Link>
              <Link href="/dashboard/admin/settings">
                <Button variant="outline">
                  <Settings className="mr-2 h-4 w-4" />
                  System Settings
                </Button>
              </Link>
            </CardContent>
          </Card>
        </TabsContent>
        <TabsContent value="notifications" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>System Notifications</CardTitle>
              <CardDescription>Important system alerts and notifications</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {[
                  {
                    title: "System Maintenance",
                    message: "Scheduled maintenance on April 30, 2025 from 2:00 AM to 4:00 AM.",
                    priority: "High",
                    date: "Apr 20, 2025",
                  },
                  {
                    title: "End of Semester",
                    message: "Spring semester ends on May 15, 2025. All grades must be submitted by May 20.",
                    priority: "Medium",
                    date: "Apr 18, 2025",
                  },
                  {
                    title: "New Curriculum Update",
                    message: "New curriculum guidelines for the 2025-2026 academic year are now available.",
                    priority: "Medium",
                    date: "Apr 15, 2025",
                  },
                  {
                    title: "Staff Meeting",
                    message: "All faculty meeting scheduled for April 25, 2025 at 3:00 PM in the Main Hall.",
                    priority: "Low",
                    date: "Apr 10, 2025",
                  },
                ].map((notification, index) => (
                  <div key={index} className="flex items-start gap-4 rounded-lg border p-4">
                    <Bell className="mt-0.5 h-5 w-5 text-muted-foreground" />
                    <div className="flex-1 space-y-1">
                      <div className="flex items-center justify-between">
                        <p className="font-medium">{notification.title}</p>
                        <Badge
                          variant={
                            notification.priority === "High"
                              ? "destructive"
                              : notification.priority === "Medium"
                                ? "default"
                                : "outline"
                          }
                        >
                          {notification.priority}
                        </Badge>
                      </div>
                      <p className="text-sm text-muted-foreground">{notification.message}</p>
                      <p className="text-xs text-muted-foreground">{notification.date}</p>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <CardTitle>Notification Settings</CardTitle>
              <CardDescription>Configure system-wide notification settings</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                <Link href="/dashboard/admin/settings/notifications">
                  <Button className="w-full">
                    <Settings className="mr-2 h-4 w-4" />
                    Configure Notification Settings
                  </Button>
                </Link>
              </div>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  )
}
