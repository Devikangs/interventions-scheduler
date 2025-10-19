import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "Interventions Scheduler App",
  description: "Interventions Scheduler demo app created for learning",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body className="min-h-screen bg-gray-50 text-gray-900">
        <div className="mx-auto max-w-6xl p-6">
          <header className="mb-6 flex items-center justify-between">
            <h1 className="text-2xl font-semibold">Interventions Scheduler</h1>
            <nav className="space-x-4">
              <a href="/" className="hover:underline">Dashboard</a>
              <a href="/interventions" className="hover:underline">Interventions</a>
              <a href="/conflicts" className="hover:underline">Conflicts</a>
              <a href="/reports" className="hover:underline">Report</a>
            </nav>
          </header>
          {children}
        </div>
      </body>
    </html>
  );
}
