import {test} from 'node:test';
import assert from 'node:assert/strict';
import {question} from '../lib/quiz.ts';
import type {Item} from '../types/index.ts';
const item={type:'VOCABULARY',word:'食べる',reading:'たべる',meaningVi:'ăn'} as Item;
test('reverse and reading modes show the requested side',()=>{assert.equal(question(item,'vi-ja').prompt,'ăn');assert.equal(question(item,'vi-ja').answer,'食べる');assert.equal(question(item,'reading').answer,'たべる');});
test('grammar blank hides the inflected target and preserves the original answer',()=>{const grammar={...item,type:'GRAMMAR',word:'〜ことになる',exampleSentence:'大阪で働くことになりました。'} as Item;assert.equal(question(grammar,'grammar').prompt,'大阪で働く＿＿＿ました。');assert.equal(question(grammar,'grammar').answer,grammar.exampleSentence);});
test('missing reading falls back to a meaningful question',()=>{assert.equal(question({...item,reading:''},'reading').answer,'ăn');});
