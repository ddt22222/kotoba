"use client";
import {useState,useEffect,createContext,useContext,type ReactNode} from "react";
import type {Session} from "@supabase/supabase-js";
import {supabase} from "@/lib/supabase";
const Context=createContext<{session:Session|null;loading:boolean}>({session:null,loading:true});
export function AuthProvider({children}:{children:ReactNode}){const [session,setSession]=useState<Session|null>(null);const [loading,setLoading]=useState(true);
 useEffect(()=>{if(!supabase){setLoading(false);return;}let active=true;supabase.auth.getSession().then(({data})=>{if(active){setSession(data.session);setLoading(false);}});const {data}=supabase.auth.onAuthStateChange((_event,s)=>{setSession(s);setLoading(false);});return()=>{active=false;data.subscription.unsubscribe();};},[]);
 return <Context.Provider value={{session,loading}}>{children}</Context.Provider>;
}
export const useAuth=()=>useContext(Context);
