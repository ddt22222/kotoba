"use client";
import {useEffect,useRef,type ReactNode} from "react";
import {X,RefreshCw} from "lucide-react";
import {vi} from "@/lib/vi";
export function Badge({level}:{level:string|null}){return level?<span className={`badge ${level.toLowerCase()}`}>{level}</span>:null;}
export function Loading(){return <div role="status" className="loading"><span className="spinner"/>{vi.loading}</div>;}
export function Failure({message,retry}:{message:string;retry:()=>void}){return <div className="empty" role="alert"><p>{message}</p><button onClick={retry}><RefreshCw size={16}/>{vi.retry}</button></div>;}
export function Empty({children}:{children?:ReactNode}){return <div className="empty">{children||vi.empty}</div>;}
export function Modal({title,onClose,children}:{title:string;onClose:()=>void;children:ReactNode}){
 const ref=useRef<HTMLDialogElement>(null);useEffect(()=>{const el=ref.current;el?.showModal();return()=>el?.close();},[]);
 return <dialog ref={ref} onCancel={onClose} onClick={e=>{if(e.target===e.currentTarget)onClose();}}><div className="dialog-body"><header><h2>{title}</h2><button className="icon" onClick={onClose} aria-label="Đóng"><X/></button></header>{children}</div></dialog>;
}
export function PageHeader({eyebrow,title,description,children}:{eyebrow?:string;title:string;description?:string;children?:ReactNode}){return <header className="page-heading"><div>{eyebrow&&<p className="eyebrow">{eyebrow}</p>}<h1>{title}</h1>{description&&<p className="muted">{description}</p>}</div>{children}</header>;}
