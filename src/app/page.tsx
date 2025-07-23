"use client"

import { useState } from "react"
import { Card } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { Label } from "@/components/ui/label"

export default function Home() {
  const [username, setUsername] = useState("")
  const [password, setPassword] = useState("")

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault()
    // TODO: Implement login logic
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <Card className="w-full max-w-md p-8 space-y-8 bg-white shadow-lg rounded-lg">
        <div className="text-center">
          <h1 className="text-3xl font-bold text-blue-600 mb-2">تراڤيلكس</h1>
          <p className="text-gray-600 text-lg">
            سجل دخولك للوصول إلى بيانات رحلتك بسهولة
          </p>
        </div>

        <form onSubmit={handleLogin} className="mt-8 space-y-6">
          <div className="space-y-4">
            <div>
              <Label htmlFor="username" className="block text-right">
                اسم المستخدم
              </Label>
              <Input
                id="username"
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                className="mt-1 text-right"
                placeholder="أدخل اسم المستخدم"
                required
              />
            </div>

            <div>
              <Label htmlFor="password" className="block text-right">
                كلمة المرور
              </Label>
              <Input
                id="password"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="mt-1 text-right"
                placeholder="أدخل كلمة المرور"
                required
              />
            </div>
          </div>

          <Button type="submit" className="w-full bg-blue-600 hover:bg-blue-700">
            تسجيل الدخول
          </Button>

          <div className="text-center mt-4">
            <p className="text-gray-600">
              ليس لديك حساب؟{" "}
              <a href="/register" className="text-blue-600 hover:underline font-bold">
                سجل الآن
              </a>
            </p>
          </div>
        </form>
      </Card>
    </div>
  )
}
