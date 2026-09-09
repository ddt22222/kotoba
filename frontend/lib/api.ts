import {supabase} from "./supabase";
export class ApiError extends Error {constructor(message:string,public status:number){super(message);}}
export async function api<T>(path:string,options:RequestInit={}):Promise<T>{
 const base=process.env.NEXT_PUBLIC_API_URL;
 if(!base||!supabase)throw new ApiError("Chưa cấu hình kết nối ứng dụng.",503);
 const {data:{session}}=await supabase.auth.getSession();
 if(!session)throw new ApiError("Vui lòng đăng nhập.",401);
 const response=await fetch(`${base.replace(/\/$/,"")}${path}`,{...options,cache:"no-store",headers:{"Content-Type":"application/json",...options.headers,Authorization:`Bearer ${session.access_token}`}});
 if(!response.ok){const body=await response.json().catch(()=>({}));throw new ApiError(body.detail||"Không thể thực hiện. Vui lòng thử lại.",response.status);}
 return response.status===204?undefined as T:response.json() as Promise<T>;
}
export const json=(method:string,body:unknown):RequestInit=>({method,body:JSON.stringify(body)});
