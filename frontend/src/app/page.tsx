import { get } from "@/lib/api";

type Intervention = { id:number; title:string; location:string; accelerator:string; startAt:string; endAt:string; status:string; type:string; requiredSkills:string[]; };

export default async function Page() {
  const data = await get<Intervention[]>("/api/v1/interventions");
  const in7d = new Date(Date.now()+7*24*3600*1000);
  return (
    <main>
      <h2 className="mb-4 text-xl font-medium">Overview</h2>
      <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
        <div className="rounded-xl border bg-white p-4"><div className="text-sm">Total interventions</div><div className="text-3xl">{data.length}</div></div>
        <div className="rounded-xl border bg-white p-4"><div className="text-sm">Upcoming (7 days)</div><div className="text-3xl">{data.filter(i=>new Date(i.startAt)<in7d).length}</div></div>
      </div>
    </main>
  );
}
