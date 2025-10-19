import { get, post } from "@/lib/api";

type Intervention = {
  id?: number; title:string; location:string; accelerator:string;
  startAt:string; endAt:string; type:string; status:string;
  requiredSkills:string[]; notes?:string;
};

async function create(formData: FormData) {
  "use server";
  const toIso = (v: unknown) => {
      const s = String(v || "").trim();
      if (!s) return "";
      // Ensure seconds so Safari/Firefox parse reliably
      const withSeconds = s.length === 16 ? `${s}:00` : s;
      return new Date(withSeconds).toISOString();
  };

  const startAtIso = toIso(formData.get("startAt"));
  const endAtIso   = toIso(formData.get("endAt"));

  const payload = {
    title: String(formData.get("title") || ""),
    location: String(formData.get("location") || ""),
    accelerator: String(formData.get("accelerator") || "LHC"),
    startAt: startAtIso,
    endAt: endAtIso,
    type: String(formData.get("type") || "maintenance"),
    status: String(formData.get("status") || "planned"),
    requiredSkills: String(formData.get("skills") || "")
      .split(",")
      .map(s => s.trim())
      .filter(Boolean),
    notes: String(formData.get("notes") || "")
  };
  if (startAtIso && endAtIso && new Date(endAtIso) <= new Date(startAtIso)) {
      throw new Error("End time must be after start time");
  }
  await post<Intervention>("/api/v1/interventions", payload);
}

export default async function Page() {
  const items = await get<Intervention[]>("/api/v1/interventions");
  return (
    <main className="grid grid-cols-1 gap-6 md:grid-cols-2">
      <section className="rounded-xl border bg-white p-4">
        <h2 className="mb-3 text-lg font-medium">Create intervention</h2>
        <form action={create} className="space-y-3">
          <input name="title" placeholder="Title" className="w-full rounded border p-2" required />
          <div className="grid grid-cols-2 gap-3">
            <input name="location" placeholder="Location" className="rounded border p-2" required />
            <input name="accelerator" placeholder="Accelerator (e.g. LHC)" className="rounded border p-2" required />
          </div>
          <div className="grid grid-cols-2 gap-3">
            <input type="datetime-local" name="startAt" className="rounded border p-2" required />
            <input type="datetime-local" name="endAt" className="rounded border p-2" required />
          </div>
          <div className="grid grid-cols-2 gap-3">
            <input name="type" placeholder="Type" className="rounded border p-2" required />
            <input name="status" placeholder="Status" className="rounded border p-2" required />
          </div>
          <input name="skills" placeholder="Required skills (comma-separated)" className="w-full rounded border p-2" />
          <textarea name="notes" placeholder="Notes" className="w-full rounded border p-2" />
          <button className="rounded bg-black px-4 py-2 text-white">Create</button>
        </form>
      </section>

      <section className="rounded-xl border bg-white p-4">
        <h2 className="mb-3 text-lg font-medium">Interventions</h2>
        <ul className="divide-y">
          {items.map(i=>(
            <li key={i.id} className="py-3">
              <div className="font-medium">{i.title}</div>
              <div className="text-sm text-gray-600">{i.accelerator} • {i.location} • {new Date(i.startAt).toLocaleString()} → {new Date(i.endAt).toLocaleString()}</div>
              <div className="text-sm">skills: {i.requiredSkills?.join(", ")}</div>
            </li>
          ))}
        </ul>
      </section>
    </main>
  );
}
