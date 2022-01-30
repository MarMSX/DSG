-= MarMSX S4b - MSX Sample Player =-

Author: Marcelo Silveira
Language: Assemby Z-80 for MSX computers
Country: Brazil
E-mail: flamar98 at hotmail.com
Project home-page: http://marmsx.msxall.com/projetos/dsg/english.php
License: GNU/GPL v.3.x - http://www.gnu.org/licenses/gpl-3.0.txt

Short description:

Plays 4-bit samples on MSX 1 or better.


Add  Machine code Line          Mnemonics         Comments
-------------------------------------------------------------------------------------
D000  		   10 		ORG  &HD000	; Initial address
D000  F3	   20 		DI 		; Disable interruptions
D001  3E 07	   30 		LD   A,7	; Set register 7 (mixer)
D003  D3 A0	   40 		OUT  (&HA0),A	;
D005  3E BE	   50 		LD   A,190	; Mixer value - enable channel A
D007  D3 A1	   60 		OUT  (&HA1),A	;
D009  AF	   70 		XOR  A		; Register 0
D00A  D3 A0	   80 		OUT  (&HA0),A	;
D00C  3E 00	   90 		LD   A,0	; LO period in A = 0
D00E  D3 A1	  100 		OUT  (&HA1),A	;
D010  3E 01	  110 		LD   A,1	; Register 1
D012  D3 A0	  120 		OUT  (&HA0),A	;
D014  AF	  130 		XOR  A		; HI period in A = 0
D015  D3 A1	  140 		OUT  (&HA1),A	;
D017  DB A8	  150 		IN   A,(&HA8)	; Read slots configuration
D019  5F	  160 		LD   E,A	; Save in E
D01A  06 04	  170 		LD   B,4	;
D01C  CB 3F	  180 ROT:	SRL  A		; Do A >> 4
D01E  10 FC	  190 		DJNZ ROT	;
D020  83	  200 		ADD  A,E	; Join values
D021  D3 A8	  210 		OUT  (&HA8),A	; Set all slots as RAM
D023  21 00 00	  220 		LD   HL,0	; Data initial address
D026  06 02	  230 LOOP:	LD   B,2	; Read 2 nibbles
D028  1E 00	  240 		LD   E,0	; E stores A
D02A  3E 08	  250 LPI:	LD   A,8	; Selects register 8
D02C  D3 A0	  260 		OUT  (&HA0),A	;
D02E  7B	  270 		LD   A,E	; Recover saved value
D02F  ED 6F	  280 		RLD 		; Rotates A with (HL)
D031  D3 A1	  290 		OUT  (&HA1),A	; Change volume
D033  5F	  300 		LD   E,A	; Save A in E
D034  16 0E	  310 		LD   D,14	;
D036  15	  320 DELAY:	DEC  D		; Delay of 14
D037  20 FD	  330 		JR   NZ,DELAY	;
D039  10 F4	  340 		DJNZ LPI	; Next B
D03B  ED 6F	  350 		RLD 		; Additional to recover (HL)
D03D  23	  360 		INC  HL		; Next sound data
D03E  7C	  370 		LD   A,H	; 
D03F  FE 40	  380 		CP   &H40	; Check if HL=end
D041  20 E3	  390 		JR   NZ,LOOP	; If not, LOOP
D043  3E 08	  400 		LD   A,8	; Register 8
D045  D3 A0	  410 		OUT  (&HA0),A	;
D047  AF	  420 		XOR  A		; Volume 0
D048  D3 A1	  430 		OUT  (&HA1),A	;
D04A  DB A8	  440 		IN   A,(&HA8)	; Read slots
D04C  E6 F0	  450 		AND  &HF0	; Do RAM RAM ROM ROM
D04E  D3 A8	  460 		OUT  (&HA8),A	;
D050  FB	  470 		EI 		; Enable interruptions
D051  C9	  480 		RET 		; Return to Basic
