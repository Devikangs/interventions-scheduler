export const API_BASE =
  process.env.NEXT_PUBLIC_API_BASE ?? "http://localhost:8080";

async function json<T>(res: Response): Promise<T> {

  if (!res.ok) throw new Error(`${res.status} ${await res.text().catch(()=>res.statusText)}`);
  return res.json();
}

export async function get<T>(path: string) {
  return json<T>(await fetch(`${API_BASE}${path}`, { cache: "no-store" }));
}

export async function post<T>(path: string, body?: unknown) {

  console.log('path',path)
  return json<T>(await fetch(`${API_BASE}${path}`, {
    method: "POST",
    headers: { "content-type": "application/json" },
    body: body ? JSON.stringify(body) : undefined,
  }));
}
