 import { NextRequest, NextResponse } from "next/server";

 export async function POST(req: NextRequest) {
   const { searchParams } = new URL(req.url);
   const from = searchParams.get("from");
   const to   = searchParams.get("to");
   const constraints = searchParams.get("constraints") ?? "";

   const backend = `${process.env.NEXT_PUBLIC_API_BASE}/api/v1/reports/schedule` +
     `?from=${encodeURIComponent(from ?? "")}` +
     `&to=${encodeURIComponent(to ?? "")}` +
     `&constraints=${encodeURIComponent(constraints)}`;

   const beResp = await fetch(backend, {
     method: "POST",
     headers: { "Accept": "application/json" },
   });

   const bodyText = await beResp.text();
   const ct = beResp.headers.get("content-type") || "";
   return new NextResponse(
     bodyText,
     {
       status: beResp.status,
       headers: { "content-type": ct || "text/plain" }
     }
   );
 }
