import {createClient} from "@supabase/supabase-js";
const url=process.env.NEXT_PUBLIC_SUPABASE_URL;
const key=process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY;
export const configured=Boolean(url&&key&&process.env.NEXT_PUBLIC_API_URL);
export const supabase=url&&key?createClient(url,key):null;
