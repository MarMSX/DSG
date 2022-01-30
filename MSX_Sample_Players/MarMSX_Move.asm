-= MarMSX Move - MSX Sample Player =-

Author: Marcelo Silveira
Language: Assemby Z-80 for MSX computers
Country: Brazil
E-mail: flamar98 at hotmail.com
Project home-page: http://marmsx.msxall.com/projetos/dsg/english.php
License: GNU/GPL v.3.x - http://www.gnu.org/licenses/gpl-3.0.txt

Short description:

This program moves a file from MSX RAM address &H9000 to &H0000.
Needed by other samples on MarMSX DSG Project.


Add  Machine code Line          Mnemonics         Comments
-------------------------------------------------------------------------------------
D100  		   10 		ORG  &HD100	; Program initial address
D100  F3	   20 		DI 		; Disable interruptions
D101  DB A8	   30 		IN   A,(&HA8)	; Read slots configuration
D103  5F	   40 		LD   E,A	; Save in E
D104  06 04	   50 		LD   B,4	; 
D106  CB 3F	   60 ROT1:	SRL  A		; Do A >> 4
D108  10 FC	   70 		DJNZ ROT1	;
D10A  83	   80 		ADD  A,E	; Join A and E
D10B  D3 A8	   90 		OUT  (&HA8),A	; Configure RAM in all slots
D10D  21 00 90	  100 		LD   HL,&H9000	; Source
D110  11 00 00	  110 		LD   DE,&H0000	; Destiny
D113  01 00 40	  120 		LD   BC,&H4000	; Length
D116  ED B0	  130 		LDIR 		; Move a block
D118  DB A8	  140 		IN   A,(&HA8)	; Read slots configuration
D11A  E6 F0	  150 		AND  &HF0	; Filter to enable ROM
D11C  D3 A8	  160 		OUT  (&HA8),A	; Change to RAM RAM ROM ROM
D11E  FB	  170 		EI 		; Enable interruptions
D11F  C9	  180 		RET 		; Return to Basic
