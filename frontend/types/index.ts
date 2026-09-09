export type ItemType="VOCABULARY"|"GRAMMAR"|"KANJI"|"PERSONAL";
export type Level="N5"|"N4"|"N3"|"N2"|"N1";
export type Status="NEW"|"LEARNING"|"REVIEW"|"MASTERED";
export interface Item {id:string;type:ItemType;word:string;reading:string;meaningVi:string;jlptLevel:Level|null;partOfSpeech:string;exampleSentence:string;exampleReading:string;exampleMeaningVi:string;notes:string;formation:string;explanation:string;onyomi:string;kunyomi:string;strokeCount:number|null;mnemonic:string;extraExamples:string;createdAt:string;updatedAt:string;status:Status;favorite:boolean;nextReviewAt:string|null;version:number}
export interface PersonalInput {word:string;meaningVi:string;exampleSentence:string;reading:string;jlptLevel:Level|null;notes:string;version:number}
export interface Page<T> {content:T[];totalElements:number;totalPages:number;number:number;size:number}
export interface Settings {timezone:string;dailyGoal:number;theme:"light"|"dark"|"system";targetLevel:Level}
export interface Stats {vocabularyLearned:number;grammarLearned:number;kanjiLearned:number;personalLearned:number;totalReviews:number;accuracy:number;streak:number;todayReviews:number;due:number;dailyGoal:number;levels:{level:Level;type:ItemType;total:number;learned:number}[];days:{date:string;reviews:number;correct:number}[];recent:Item[]}
