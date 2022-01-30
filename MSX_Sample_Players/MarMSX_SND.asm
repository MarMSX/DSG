-= MarMSX SND - MSX Sample Player =-

Author: Marcelo Silveira
Language: Assemby Z-80 for MSX computers
Country: Brazil
E-mail: flamar98 at hotmail.com
Project home-page: http://marmsx.msxall.com/projetos/dsg/english.php
License: GNU/GPL v.3.x - http://www.gnu.org/licenses/gpl-3.0.txt.

Short description:

Plays 1-bit samples on MSX 1 or better.


Add  Machine code Line          Mnemonics         Comments
-------------------------------------------------------------------------------------
D000  		   10 		ORG  &HD000	; Starting address
D000  F3	   20 		DI 		; Disable interruption
D001  DB A8	   30 		IN   A,(&HA8)	; Read slots configuration
D003  5F	   40 		LD   E,A	; Save on register E
D004  06 04	   50 		LD   B,4	; 
D006  CB 3F	   60 ROT:	SRL  A		; Shift left A 4 times
D008  10 FC	   70 		DJNZ ROT	;
D00A  83	   80 		ADD  A,E	; Add A to E
D00B  D3 A8	   90 		OUT  (&HA8),A	; Set all slots as RAM
D00D  21 00 00	  100 		LD   HL,0	; PCM initial data
D010  4E	  110 LOOP:	LD   C,(HL)	; Read the next octet (8 samples)
D011  06 08	  120 		LD   B,8	; Repeat 8x
D013  CB 11	  130 LPI:	RL   C		; Shift 1 bit to left
D015  DB AA	  140 		IN   A,(&HAA)	; Read PPI port C state
D017  38 04	  150 		JR   C,TONE	; Check if CY=1
D019  E6 7F	  160 		AND  &H7F	; If not, click=0
D01B  18 02	  170 		JR   CHTONE	; Jumps to CHtone
D01D  F6 80	  180 TONE:	OR   &H80	; If yes, click=1
D01F  D3 AA	  190 CHTONE:	OUT  (&HAA),A	; Sen result to PPI
D021  1E 0E	  200 		LD   E,14	; Create a delay of 14
D023  1D	  210 DELAY:	DEC  E		;
D024  20 FD	  220 		JR   NZ,DELAY	; Delay
D026  10 EB	  230 		DJNZ LPI	;
D028  23	  240 		INC  HL		; Next octet (data)
D029  7C	  250 		LD   A,H	;
D02A  FE 40	  260 		CP   &H40	; Check if HL=&H4000
D02C  20 E2	  270 		JR   NZ,LOOP	; If not, go to LOOP
D02E  DB A8	  280 		IN   A,(&HA8)	; Read slots configuration
D030  E6 F0	  290 		AND  &HF0	; Filter to set ROM on pgs 0 and 1
D032  D3 A8	  300 		OUT  (&HA8),A	; Change
D034  FB	  310 		EI 		; Enable interruptions
D035  C9	  320 		RET		; Return to Basic
