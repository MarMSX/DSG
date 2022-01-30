-= MarMSX S4b Plus - MSX Sample Player =-

Author: Marcelo Silveira
Language: Assemby Z-80 for MSX computers
Country: Brazil
E-mail: flamar98 at hotmail.com
Project home-page: http://marmsx.msxall.com/projetos/dsg/english.php
License: GNU/GPL v.3.x - http://www.gnu.org/licenses/gpl-3.0.txt

Short description:

Plays 4-bit samples on MSX 1 or better.
This player uses the three channels of the PSG sound chip.


Add  Machine code Line          Mnemonics                 Comments
-------------------------------------------------------------------------------------
D000  		   10 		ORG  &HD000		; Initial address
D000  F3	   20 		DI 			; Disable interruptions
D001  3E 07	   30 		LD   A,7		; Set register 7 (mixer)
D003  D3 A0	   40 		OUT  (&HA0),A		;
D005  3E B8	   50 		LD   A,&B10111000	; Mix value
D007  D3 A1	   60 		OUT  (&HA1),A		;  set channels A,B,C
D009  06 07	   70 		LD   B,7		;
D00B  78   	   80 LPSG:	LD   A,B		;
D00C  3D   	   90 		DEC  A			; Set all periods
D00D  D3 A0	  100 		OUT  (&HA0),A		; 
D00F  AF   	  110 		XOR  A			; to value zero
D010  D3 A1	  120 		OUT  (&HA1),A		;
D012  10 F7	  130 		DJNZ LPSG		;
D014  DB A8	  140 		IN   A,(&HA8)		; Read slot conf.
D016  5F	  150 		LD   E,A		; Save in E
D017  06 04	  160 		LD   B,4		;
D019  CB 3F	  170 ROT:	SRL  A			; Do A >> 4
D01B  10 FC	  180 		DJNZ ROT		;
D01D  83	  190 		ADD  A,E		; Join values
D01E  D3 A8	  200 		OUT  (&HA8),A		; Set all slots as RAM
D020  21 00 00	  210 		LD   HL,0		; Data initial address
D023  06 02	  220 LOOP:	LD   B,2		; Read 2 nibbles
D025  1E 00	  230 		LD   E,0		; E stores A
D027  7B	  240 LPI:	LD   A,E		; Recover saved value
D028  ED 6F	  250 		RLD 			; Rotates A with (HL)
D02A  4F   	  260 		LD   C,A 		; Save in C
D02B  3E 08	  270 		LD   A,8		; Select register 8 (ch. A)
D02D  D3 A0	  280 		OUT  (&HA0),A		;
D02F  79   	  290 		LD   A,C 		; Recover A
D030  D3 A1	  300 		OUT  (&HA1),A		; Change volume
D032  3E 09	  310 		LD   A,9		; Select register 9 (ch. B)
D034  D3 A0	  320 		OUT  (&HA0),A		;
D036  79   	  330 		LD   A,C 		; Recover A
D037  D3 A1	  340 		OUT  (&HA1),A		; Change volume
D039  3E 0A	  350 		LD   A,10		; Select register 10 (ch. C)
D03B  D3 A0	  360 		OUT  (&HA0),A		;
D03D  79   	  370 		LD   A,C 		; Recover A
D03E  D3 A1	  380 		OUT  (&HA1),A		; Change volume
D040  5F	  390 		LD   E,A		; Save A in E
D041  16 0E	  400 		LD   D,14		;
D043  15	  410 DELAY:	DEC  D			; Delay of 14
D044  20 FD	  420 		JR   NZ,DELAY		;
D046  10 DF	  430 		DJNZ LPI		; Next B
D048  ED 6F	  440 		RLD 			; Rotate to recover (HL)
D04A  23	  450 		INC  HL			; Next sound data
D04B  7C	  460 		LD   A,H		; 
D04C  FE 40	  470 		CP   &H40		; Check if HL=end
D04E  20 D3	  480 		JR   NZ,LOOP		; If not, LOOP
D050  DB A8	  490 		IN   A,(&HA8)		; Read slots
D052  E6 F0	  500 		AND  &HF0		; Do RAM RAM ROM ROM
D054  D3 A8	  510 		OUT  (&HA8),A		;
D056  FB	  520 		EI 			; Enable interruptions
D057  CD 90 00	  530 		CALL &H90		; Reset PSG (clear volumes)
D05A  C9	  540 		RET 			; Return to Basic
