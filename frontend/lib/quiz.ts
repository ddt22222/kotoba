import type {Item} from "@/types";
export type Mode="ja-vi"|"vi-ja"|"reading"|"grammar";
const grammar:Record<string,RegExp>={"〜たい":/たい/,"〜てもいい":/てもいい/,"〜ことがある":/ことがあり/,"〜ながら":/ながら/,"〜ことになる":/ことになり/,"〜ようにする":/ようにして/,"〜わけではない":/わけではあり/,"〜に限らず":/に限らず/,"〜に至るまで":/に至るまで/,"〜を余儀なくされる":/を余儀なくされ/};
export function question(item:Item,mode:Mode):{prompt:string;answer:string;hint:string}{
 if(mode==='grammar'&&item.type==='GRAMMAR'){const pattern=grammar[item.word];if(pattern&&pattern.test(item.exampleSentence))return {prompt:item.exampleSentence.replace(pattern,'＿＿＿'),answer:item.exampleSentence,hint:'Điền mẫu ngữ pháp phù hợp'};}
 if(mode==='vi-ja')return {prompt:item.meaningVi,answer:item.word,hint:'Bạn nói điều này bằng tiếng Nhật thế nào?'};
 if(mode==='reading'&&item.reading)return {prompt:item.word,answer:item.reading,hint:'Cách đọc là gì?'};
 return {prompt:item.word,answer:item.meaningVi,hint:'Nghĩa tiếng Việt là gì?'};
}
