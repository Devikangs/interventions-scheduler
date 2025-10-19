"use client";
import { useState } from "react";

export default function ReportsPage() {
  const [loading, setLoading] = useState(false);
  const [error, setError]     = useState<string | null>(null);
  const [text, setText]       = useState<string>("");

  const submit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setText("");

    try {
      const fd = new FormData(e.currentTarget);
      const toIso = (v: string) => new Date(v.length === 16 ? `${v}:00` : v).toISOString();
      const from = fd.get("from") ? toIso(String(fd.get("from"))) : new Date().toISOString();
      const to   = fd.get("to")   ? toIso(String(fd.get("to")))   : new Date(Date.now() + 7*86400000).toISOString();
      const constraints = String(fd.get("constraints") || "");

      const resp = await fetch(
        `/api/v1/reports/schedule?from=${encodeURIComponent(from)}&to=${encodeURIComponent(to)}&constraints=${encodeURIComponent(constraints)}`,
        { method: "POST" }
      );

      if (!resp.ok) {
        const t = await resp.text().catch(() => "");
        throw new Error(`Request failed: ${resp.status} ${resp.statusText} ${t}`);
      }

      const ct = resp.headers.get("content-type") || "";
      if (ct.includes("application/json")) {
        const txt = await resp.text();
        const safe = txt.trim().length ? JSON.parse(txt) : {}; // avoid JSON parse on empty
           setText(JSON.stringify(safe, null, 2) || "✓ Done");
      } else {
          const t = await resp.text();
          setText(t || "✓ Done");
      }
    } catch (err: any) {
      console.error("Schedule request error:", err);
      setError(err?.message ?? String(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="grid gap-6">
      <section className="rounded-xl border bg-white p-4">
        <h2 className="mb-3 text-lg font-medium">Generate AI schedule report</h2>
        <form onSubmit={submit} className="space-y-3">
          <div className="grid grid-cols-2 gap-3">
            <input type="datetime-local" name="from" className="rounded border p-2" />
            <input type="datetime-local" name="to" className="rounded border p-2" />
          </div>
          <textarea name="constraints" placeholder="Constraints/notes for AI…" className="w-full rounded border p-2" />
          <button className="rounded bg-black px-4 py-2 text-white disabled:opacity-60" disabled={loading}>
            {loading ? "Generating…" : "Generate"}
          </button>
        </form>
        {error && <div className="mt-3 rounded-md border border-red-200 bg-red-50 p-3 text-red-700">{error}</div>}
      </section>
      {text && <section className="rounded-xl border bg-white p-4 whitespace-pre-wrap">{text}</section>}
    </main>
  );
}
