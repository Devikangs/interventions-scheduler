import { NextRequest, NextResponse } from "next/server";

export const runtime = "nodejs";
export const dynamic = "force-dynamic";

export async function POST(req: NextRequest) {
  const { searchParams } = new URL(req.url);
  const from = searchParams.get("from") ?? "";
  const to = searchParams.get("to") ?? "";
  const constraints = searchParams.get("constraints") ?? "";

  const backendBase =
    process.env.BACKEND_BASE?.replace(/\/+$/, "") || "http://localhost:8080";

  const beUrl =
    `${backendBase}/api/v1/reports/schedule` +
    `?from=${encodeURIComponent(from)}` +
    `&to=${encodeURIComponent(to)}` +
    `&constraints=${encodeURIComponent(constraints)}`;

  const beResp = await fetch(beUrl, { method: "POST", headers: { Accept: "application/json" } });
  const body = await beResp.text();
  const ct = beResp.headers.get("content-type") || "text/plain";
  return new NextResponse(body, { status: beResp.status, headers: { "content-type": ct } });
}
