"use client";
import {useState,useEffect,useCallback} from "react";
import {api} from "./api";
export function useResource<T>(path:string){
 const [data,setData]=useState<T>();const [error,setError]=useState("");const [loading,setLoading]=useState(true);const [revision,setRevision]=useState(0);
 const reload=useCallback(()=>setRevision(v=>v+1),[]);
 useEffect(()=>{const controller=new AbortController();setLoading(true);setError("");api<T>(path,{signal:controller.signal}).then(result=>{if(!controller.signal.aborted)setData(result);}).catch(e=>{if(!controller.signal.aborted)setError(e instanceof Error?e.message:"Không thể tải dữ liệu.");}).finally(()=>{if(!controller.signal.aborted)setLoading(false);});return()=>controller.abort();},[path,revision]);
 return {data,error,loading,reload,setData};
}
export function useDebounced(value:string,delay=250){const [v,setV]=useState(value);useEffect(()=>{const t=setTimeout(()=>setV(value),delay);return()=>clearTimeout(t);},[value,delay]);return v;}
