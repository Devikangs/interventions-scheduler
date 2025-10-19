import { get } from "@/lib/api";

type Conflict = { interventionA: number; interventionB: number; reason: string };
type Cluster  = { location: string; interventionIds: number[]; summary: string };
type Tech     = { technicianId: number; techA: number; techB: number; reason: string };

const toIso = (d: Date) => d.toISOString();

const toLocalInput = (d: Date) => {
  const pad = (n: number) => String(n).padStart(2, "0");
  const yyyy = d.getFullYear();
  const mm   = pad(d.getMonth() + 1);
  const dd   = pad(d.getDate());
  const hh   = pad(d.getHours());
  const mi   = pad(d.getMinutes());
  return `${yyyy}-${mm}-${dd}T${hh}:${mi}`;
};

const parseLocalInput = (s?: string | null) => {
  if (!s) return undefined;
  const fixed = s.length === 16 ? `${s}:00` : s;
  const d = new Date(fixed);
  return isNaN(d.getTime()) ? undefined : d;
};

const defaultFrom = () => new Date();
const defaultTo   = () => new Date(Date.now() + 7 * 24 * 3600 * 1000);

export default async function Page({
  searchParams,
}: {
  searchParams?: { from?: string; to?: string };
}) {

  const fromInput = searchParams?.from;
  const toInput   = searchParams?.to;

  const fromDate = parseLocalInput(fromInput) ?? defaultFrom();
  const toDate   = parseLocalInput(toInput)   ?? defaultTo();

  let from = fromDate;
  let to   = toDate;
  if (to.getTime() <= from.getTime()) {
    to = new Date(from.getTime() + 60 * 60 * 1000);
  }


  const fromIso = toIso(from);
  const toIsoStr = toIso(to);


  const [pairwise, clusters, tech] = await Promise.all([
    get<Conflict[]>(
      `/api/v1/interventions/conflicts?from=${encodeURIComponent(fromIso)}&to=${encodeURIComponent(toIsoStr)}`
    ),
    get<Cluster[]>(
      `/api/v1/interventions/conflict-clusters?from=${encodeURIComponent(fromIso)}&to=${encodeURIComponent(toIsoStr)}`
    ),
    get<Tech[]>(
      `/api/v1/interventions/tech-conflicts?from=${encodeURIComponent(fromIso)}&to=${encodeURIComponent(toIsoStr)}`
    ),
  ]);


  const fromPrefill = toLocalInput(from);
  const toPrefill   = toLocalInput(to);

  return (
    <main className="grid grid-cols-1 gap-6">
      <section className="rounded-xl border bg-white p-4">
        <form method="get" className="flex flex-col gap-3 md:flex-row md:items-end">
          <div className="flex flex-col">
            <label className="text-sm text-gray-700">From</label>
            <input
              type="datetime-local"
              name="from"
              defaultValue={fromPrefill}
              className="rounded border p-2"
              required
            />
          </div>
          <div className="flex flex-col">
            <label className="text-sm text-gray-700">To</label>
            <input
              type="datetime-local"
              name="to"
              defaultValue={toPrefill}
              className="rounded border p-2"
              required
            />
          </div>

          <div className="flex gap-2 md:ml-3">
            <button className="rounded bg-black px-4 py-2 text-white" type="submit">
              Apply
            </button>

            <a
              className="rounded border px-3 py-2"
              href={`?from=${encodeURIComponent(toLocalInput(defaultFrom()))}&to=${encodeURIComponent(
                toLocalInput(new Date(Date.now() + 3 * 24 * 3600 * 1000))
              )}`}
            >
              Next 3 days
            </a>
            <a
              className="rounded border px-3 py-2"
              href={`?from=${encodeURIComponent(toLocalInput(defaultFrom()))}&to=${encodeURIComponent(
                toLocalInput(new Date(Date.now() + 7 * 24 * 3600 * 1000))
              )}`}
            >
              Next 7 days
            </a>
          </div>
        </form>
      </section>


      <section className="grid grid-cols-1 gap-6 md:grid-cols-3">
        <div className="rounded-xl border bg-white p-4">
          <h2 className="mb-2 font-medium">Pairwise conflicts</h2>
          {pairwise.length === 0 ? (
            <div className="text-sm text-gray-500">No conflicts in this window.</div>
          ) : (
            <ul className="list-disc pl-5">
              {pairwise.map((c, i) => (
                <li key={i}>
                  Interventions #{c.interventionA} ↔ #{c.interventionB} — {c.reason}
                </li>
              ))}
            </ul>
          )}
        </div>

        <div className="rounded-xl border bg-white p-4">
          <h2 className="mb-2 font-medium">Clusters</h2>
          {clusters.length === 0 ? (
            <div className="text-sm text-gray-500">No clusters in this window.</div>
          ) : (
            <ul className="list-disc pl-5">
              {clusters.map((c, i) => (
                <li key={i}>
                  {c.summary}: {c.interventionIds.join(", ")}
                </li>
              ))}
            </ul>
          )}
        </div>

        <div className="rounded-xl border bg-white p-4">
          <h2 className="mb-2 font-medium">Tech double-bookings</h2>
          {tech.length === 0 ? (
            <div className="text-sm text-gray-500">No technician conflicts in this window.</div>
          ) : (
            <ul className="list-disc pl-5">
              {tech.map((t, i) =>(
                     <li key={i}>
                       Tech {t.technicianId}: Interventions #{t.techA} ↔ #{t.techB} — {t.reason}
                     </li>
                   ))}
            </ul>
          )}
        </div>
      </section>
    </main>
  );
}
