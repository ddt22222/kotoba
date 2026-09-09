import type {Metadata} from "next";
import type {ReactNode} from "react";
import {AuthProvider} from "@/components/auth-provider";
import "./globals.css";
export const metadata:Metadata={title:{default:"Kotoba · Học tiếng Nhật mỗi ngày",template:"%s · Kotoba"},description:"Học từ vựng, ngữ pháp và Kanji N5–N1. Lưu từ mới và ôn tập mỗi ngày."};
export default function RootLayout({children}:{children:ReactNode}){return <html lang="vi" suppressHydrationWarning><body><a className="skip" href="#main">Đến nội dung chính</a><AuthProvider>{children}</AuthProvider></body></html>;}
