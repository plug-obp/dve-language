#!/bin/bash

divine combine -o ./adding/adding.mdve  VAL=17 MAX=20  > ./generated/adding.1.dve
divine combine -o ./adding/adding.mdve  VAL=500 MAX=200  > ./generated/adding.2.dve
divine combine -o ./adding/adding.mdve  VAL=637 MAX=300  > ./generated/adding.3.dve
divine combine -o ./adding/adding.mdve  VAL=749 MAX=400  > ./generated/adding.4.dve
divine combine -o ./adding/adding.mdve  VAL=849 MAX=500  > ./generated/adding.5.dve
divine combine -o ./adding/adding.mdve  VAL=949 MAX=600  > ./generated/adding.6.dve
divine combine -o ./anderson/anderson.mdve  ERROR=1 N=2  > ./generated/anderson.1.dve
divine combine -o ./anderson/anderson.mdve N=3  > ./generated/anderson.2.dve
divine combine -o ./anderson/anderson.mdve  ERROR=1 N=3  > ./generated/anderson.3.dve
divine combine -o ./anderson/anderson.mdve N=4  > ./generated/anderson.4.dve
divine combine -o ./anderson/anderson.mdve  ERROR=1 N=5  > ./generated/anderson.5.dve
divine combine -o ./anderson/anderson.mdve N=6  > ./generated/anderson.6.dve
divine combine -o ./anderson/anderson.mdve  ERROR=1 N=6  > ./generated/anderson.7.dve
divine combine -o ./anderson/anderson.mdve N=7  > ./generated/anderson.8.dve
divine combine -o ./at/at.mdve  K1=2 N=3  K2=5  > ./generated/at.1.dve
divine combine -o ./at/at.mdve  K1=2 N=3  K2=3  > ./generated/at.2.dve
divine combine -o ./at/at.mdve  K1=2 N=4  K2=4  > ./generated/at.3.dve
divine combine -o ./at/at.mdve  K1=1 N=5  K2=3  > ./generated/at.4.dve
divine combine -o ./at/at.mdve  K1=2 N=5  K2=5  > ./generated/at.5.dve
divine combine -o ./at/at.mdve  K1=3 N=5  K2=5  > ./generated/at.6.dve
divine combine -o ./at/at.mdve  K1=2 N=6  K2=5  > ./generated/at.7.dve
divine combine -o ./bakery/bakery.mdve MAX=9 N=2  > ./generated/bakery.1.dve
divine combine -o ./bakery/bakery.mdve MAX=6  ERROR=1 N=2  > ./generated/bakery.2.dve
divine combine -o ./bakery/bakery.mdve MAX=4 N=3  > ./generated/bakery.3.dve
divine combine -o ./bakery/bakery.mdve MAX=6 ERROR=1 N=3  > ./generated/bakery.4.dve
divine combine -o ./bakery/bakery.mdve MAX=7 N=4  > ./generated/bakery.5.dve
divine combine -o ./bakery/bakery.mdve MAX=5 ERROR=1 N=4  > ./generated/bakery.6.dve
divine combine -o ./bakery/bakery.mdve MAX=12 N=4  > ./generated/bakery.7.dve
divine combine -o ./bakery/bakery.mdve MAX=5 N=5  > ./generated/bakery.8.dve
divine combine -o ./blocks/blocks.mdve VERSION=60  > ./generated/blocks.2.dve
divine combine -o ./blocks/blocks.mdve VERSION=8  > ./generated/blocks.3.dve
divine combine -o ./blocks/blocks.mdve VERSION=100  > ./generated/blocks.4.dve
divine combine -o ./bopdp/bopdp.mdve MAX_AP_INTS=1  MAX_LSL_INTS=2  > ./generated/bopdp.1.dve
divine combine -o ./bopdp/bopdp.mdve MAX_AP_INTS=255  MAX_LSL_INTS=255  > ./generated/bopdp.2.dve
divine combine -o ./bopdp/bopdp.mdve MAX_AP_INTS=7  MAX_LSL_INTS=7  > ./generated/bopdp.3.dve
divine combine -o ./bridge/bridge.mdve  MAX=60 N=4  > ./generated/bridge.1.dve
divine combine -o ./bridge/bridge.mdve  MAX=140 N=6  > ./generated/bridge.2.dve
divine combine -o ./bridge/bridge.mdve  MAX=200 N=8  > ./generated/bridge.3.dve
divine combine -o ./brp/brp.mdve MAX_FRAMES=5 REPEAT=3  > ./generated/brp.1.dve
divine combine -o ./brp/brp.mdve MAX_FRAMES=3 REPEAT=5 ERROR=1  > ./generated/brp.2.dve
divine combine -o ./brp/brp.mdve MAX_FRAMES=10 REPEAT=10 ERROR=1  > ./generated/brp.3.dve
divine combine -o ./brp/brp.mdve MAX_FRAMES=20 REPEAT=20 ERROR=0  > ./generated/brp.4.dve
divine combine -o ./brp/brp.mdve MAX_FRAMES=20 REPEAT=30 ERROR=1  > ./generated/brp.5.dve
divine combine -o ./brp/brp.mdve MAX_FRAMES=30 REPEAT=23 ERROR=1  > ./generated/brp.6.dve
divine combine -o ./brp2/brp2.mdve MAX=3 TD=2  > ./generated/brp2.1.dve
divine combine -o ./brp2/brp2.mdve MAX=2 TD=2 T1=4  > ./generated/brp2.2.dve
divine combine -o ./brp2/brp2.mdve MAX=3 TD=2 TR=30  > ./generated/brp2.3.dve
divine combine -o ./brp2/brp2.mdve MAX=3 TD=5  > ./generated/brp2.4.dve
divine combine -o ./brp2/brp2.mdve MAX=7 TD=2  > ./generated/brp2.5.dve
divine combine -o ./brp2/brp2.mdve MAX=7 TD=5  > ./generated/brp2.6.dve
divine combine -o ./cambridge/cambridge.mdve LOSS=0 ERROR=0 K=3  > ./generated/cambridge.1.dve
divine combine -o ./cambridge/cambridge.mdve LOSS=1 ERROR=1 K=3  > ./generated/cambridge.2.dve
divine combine -o ./cambridge/cambridge.mdve LOSS=0 ERROR=2 K=4  > ./generated/cambridge.3.dve
divine combine -o ./cambridge/cambridge.mdve LOSS=0 ERROR=3 K=5  > ./generated/cambridge.4.dve
divine combine -o ./cambridge/cambridge.mdve LOSS=1 ERROR=0 K=10  > ./generated/cambridge.5.dve
divine combine -o ./cambridge/cambridge.mdve LOSS=0 ERROR=1 K=15  > ./generated/cambridge.6.dve
divine combine -o ./cambridge/cambridge.mdve LOSS=1 ERROR=1 K=20  > ./generated/cambridge.7.dve
divine combine -o ./collision/collision.mdve N=2  > ./generated/collision.1.dve
divine combine -o ./collision/collision.mdve ERROR=1 N=2  > ./generated/collision.2.dve
divine combine -o ./collision/collision.mdve N=3  > ./generated/collision.3.dve
divine combine -o ./collision/collision.mdve N=4  > ./generated/collision.4.dve
divine combine -o ./collision/collision.mdve ERROR=1 N=4  > ./generated/collision.5.dve
divine combine -o ./collision/collision.mdve N=5  > ./generated/collision.6.dve
divine combine -o ./cyclic_scheduler/cyclic_scheduler.mdve N=8  > ./generated/cyclic_scheduler.1.dve
divine combine -o ./cyclic_scheduler/cyclic_scheduler.mdve ERROR=1 N=5  > ./generated/cyclic_scheduler.2.dve
divine combine -o ./cyclic_scheduler/cyclic_scheduler.mdve N=13  > ./generated/cyclic_scheduler.3.dve
divine combine -o ./cyclic_scheduler/cyclic_scheduler.mdve ERROR=1 N=9  > ./generated/cyclic_scheduler.4.dve
divine combine -o ./driving_phils/driving_phils.mdve  K=1  MUTEX=1 N=2  > ./generated/driving_phils.1.dve
divine combine -o ./driving_phils/driving_phils.mdve  K=1  MUTEX=0 N=3  > ./generated/driving_phils.2.dve
divine combine -o ./driving_phils/driving_phils.mdve  DELTA=5  K=1 FAIR=1  R=15  MUTEX=1 N=3  > ./generated/driving_phils.3.dve
divine combine -o ./driving_phils/driving_phils.mdve  K=2  MUTEX=1 N=3  > ./generated/driving_phils.4.dve
divine combine -o ./driving_phils/driving_phils.mdve  DELTA=3  K=2  FAIR=1  MUTEX=0 N=3  > ./generated/driving_phils.5.dve
divine combine -o ./elevator/elevator.mdve  Persons=2 Floors=5  Strategy=0  > ./generated/elevator.1.dve
divine combine -o ./elevator/elevator.mdve  Persons=2 Floors=4  Strategy=1  > ./generated/elevator.2.dve
divine combine -o ./elevator/elevator.mdve  Persons=3 Floors=6  Strategy=1  > ./generated/elevator.3.dve
divine combine -o ./elevator/elevator.mdve  Persons=4 Floors=4  Strategy=0  > ./generated/elevator.4.dve
divine combine -o ./elevator/elevator.mdve  Persons=5 Floors=6  Strategy=1  > ./generated/elevator.5.dve
divine combine -o ./elevator2/elevator2.mdve  N=4 CONTROL=clever  > ./generated/elevator2.1.dve
divine combine -o ./elevator2/elevator2.mdve  N=10 CONTROL=naive  > ./generated/elevator2.2.dve
divine combine -o ./elevator2/elevator2.mdve  N=13 CONTROL=clever  > ./generated/elevator2.3.dve
divine combine -o ./elevator_planning/elevator_planning.mdve VERSION=2  > ./generated/elevator_planning.1.dve
divine combine -o ./elevator_planning/elevator_planning.mdve VERSION=3  > ./generated/elevator_planning.2.dve
divine combine -o ./elevator_planning/elevator_planning.mdve VERSION=44  > ./generated/elevator_planning.3.dve
divine combine -o ./exit/exit.mdve MAX=24 N=1  > ./generated/exit.1.dve
divine combine -o ./exit/exit.mdve MAX=6 N=2  > ./generated/exit.2.dve
divine combine -o ./exit/exit.mdve MAX=10 N=2  > ./generated/exit.3.dve
divine combine -o ./exit/exit.mdve MAX=24 N=2  > ./generated/exit.4.dve
divine combine -o ./exit/exit.mdve MAX=6 N=3  > ./generated/exit.5.dve
divine combine -o ./extinction/extinction.mdve  K=3 TOPOLOGY=1  > ./generated/extinction.1.dve
divine combine -o ./extinction/extinction.mdve  K=10 TOPOLOGY=1  > ./generated/extinction.2.dve
divine combine -o ./extinction/extinction.mdve  K=2 TOPOLOGY=2  > ./generated/extinction.3.dve
divine combine -o ./extinction/extinction.mdve  K=2 TOPOLOGY=3  > ./generated/extinction.4.dve
divine combine -o ./firewire_link/firewire_link.mdve APPLICATION=0 N=2  > ./generated/firewire_link.1.dve
divine combine -o ./firewire_link/firewire_link.mdve APPLICATION=1 N=2  > ./generated/firewire_link.2.dve
divine combine -o ./firewire_link/firewire_link.mdve APPLICATION=2 N=2  > ./generated/firewire_link.3.dve
divine combine -o ./firewire_link/firewire_link.mdve APPLICATION=0 N=3  > ./generated/firewire_link.4.dve
divine combine -o ./firewire_link/firewire_link.mdve APPLICATION=1 N=3  > ./generated/firewire_link.5.dve
divine combine -o ./firewire_link/firewire_link.mdve APPLICATION=2 N=3  > ./generated/firewire_link.6.dve
divine combine -o ./firewire_link/firewire_link.mdve APPLICATION=0 N=4  > ./generated/firewire_link.7.dve
divine combine -o ./firewire_tree/firewire_tree.mdve T=2  > ./generated/firewire_tree.1.dve
divine combine -o ./firewire_tree/firewire_tree.mdve T=0 N=4  > ./generated/firewire_tree.2.dve
divine combine -o ./firewire_tree/firewire_tree.mdve T=1  > ./generated/firewire_tree.3.dve
divine combine -o ./firewire_tree/firewire_tree.mdve T=0 N=7  > ./generated/firewire_tree.4.dve
divine combine -o ./firewire_tree/firewire_tree.mdve T=0 N=10  > ./generated/firewire_tree.5.dve
divine combine -o ./fischer/fischer.mdve K1=2 K2=3 N=3  > ./generated/fischer.1.dve
divine combine -o ./fischer/fischer.mdve K1=3 K2=3 N=4  > ./generated/fischer.2.dve
divine combine -o ./fischer/fischer.mdve K1=3 K2=3 N=6  > ./generated/fischer.3.dve
divine combine -o ./fischer/fischer.mdve K1=2 K2=3 N=7  > ./generated/fischer.4.dve
divine combine -o ./fischer/fischer.mdve K1=4 K2=4 N=7  > ./generated/fischer.5.dve
divine combine -o ./fischer/fischer.mdve K1=2 K2=3 N=8  > ./generated/fischer.6.dve
divine combine -o ./fischer/fischer.mdve K1=3 K2=3 N=8  > ./generated/fischer.7.dve
divine combine -o ./frogs/frogs.mdve M=17 N=1  > ./generated/frogs.1.dve
divine combine -o ./frogs/frogs.mdve M=3 N=5  > ./generated/frogs.2.dve
divine combine -o ./frogs/frogs.mdve M=31 N=1  > ./generated/frogs.3.dve
divine combine -o ./frogs/frogs.mdve M=5 N=5  > ./generated/frogs.4.dve
divine combine -o ./frogs/frogs.mdve M=5 N=6  > ./generated/frogs.5.dve
divine combine -o ./gear/gear.mdve MAX=5  > ./generated/gear.1.dve
divine combine -o ./gear/gear.mdve MAX=30  > ./generated/gear.2.dve
divine combine -o ./hanoi/hanoi.mdve N=8  > ./generated/hanoi.1.dve
divine combine -o ./hanoi/hanoi.mdve N=12  > ./generated/hanoi.2.dve
divine combine -o ./hanoi/hanoi.mdve N=15  > ./generated/hanoi.3.dve
divine combine -o ./hanoi/hanoi.mdve N=17  > ./generated/hanoi.4.dve
divine combine -o ./iprotocol/iprotocol.mdve W=2 SEQ=3  > ./generated/iprotocol.1.dve
divine combine -o ./iprotocol/iprotocol.mdve W=2 SEQ=4  > ./generated/iprotocol.2.dve
divine combine -o ./iprotocol/iprotocol.mdve W=3 SEQ=6  > ./generated/iprotocol.3.dve
divine combine -o ./iprotocol/iprotocol.mdve W=4 SEQ=8  > ./generated/iprotocol.4.dve
divine combine -o ./iprotocol/iprotocol.mdve W=5 SEQ=10  > ./generated/iprotocol.5.dve
divine combine -o ./iprotocol/iprotocol.mdve W=3 SEQ=10  > ./generated/iprotocol.6.dve
divine combine -o ./iprotocol/iprotocol.mdve W=4 SEQ=12  > ./generated/iprotocol.7.dve
divine combine -o ./krebs/krebs.mdve KREBS=1 X=5 GLUKOSA=2  > ./generated/krebs.1.dve
divine combine -o ./krebs/krebs.mdve KREBS=2 X=20 GLUKOSA=3  > ./generated/krebs.2.dve
divine combine -o ./krebs/krebs.mdve KREBS=2 X=50 GLUKOSA=10  > ./generated/krebs.3.dve
divine combine -o ./krebs/krebs.mdve KREBS=3 X=80 GLUKOSA=15  > ./generated/krebs.4.dve
divine combine -o ./lamport/lamport.mdve N=3  > ./generated/lamport.1.dve
divine combine -o ./lamport/lamport.mdve ERROR=1 N=3  > ./generated/lamport.2.dve
divine combine -o ./lamport/lamport.mdve ERROR=2 N=3  > ./generated/lamport.3.dve
divine combine -o ./lamport/lamport.mdve N=4  > ./generated/lamport.5.dve
divine combine -o ./lamport/lamport.mdve ERROR=1 N=4  > ./generated/lamport.6.dve
divine combine -o ./lamport/lamport.mdve N=5  > ./generated/lamport.7.dve
divine combine -o ./lamport/lamport.mdve ERROR=2 N=5  > ./generated/lamport.8.dve
divine combine -o ./lamport_nonatomic/lamport_nonatomic.mdve N=3  > ./generated/lamport_nonatomic.1.dve
divine combine -o ./lamport_nonatomic/lamport_nonatomic.mdve ERROR=1 N=3  > ./generated/lamport_nonatomic.2.dve
divine combine -o ./lamport_nonatomic/lamport_nonatomic.mdve ERROR=2 N=3  > ./generated/lamport_nonatomic.3.dve
divine combine -o ./lamport_nonatomic/lamport_nonatomic.mdve N=4  > ./generated/lamport_nonatomic.4.dve
divine combine -o ./lamport_nonatomic/lamport_nonatomic.mdve N=5  > ./generated/lamport_nonatomic.5.dve
divine combine -o ./lann/lann.mdve CR=0 N=3 PRECEDENCE=0 RELIABLE=1  > ./generated/lann.1.dve
divine combine -o ./lann/lann.mdve CR=1 N=4 PRECEDENCE=1 RELIABLE=1  > ./generated/lann.2.dve
divine combine -o ./lann/lann.mdve CR=1 N=4 PRECEDENCE=0 RELIABLE=0  > ./generated/lann.3.dve
divine combine -o ./lann/lann.mdve CR=0 N=5 PRECEDENCE=1 RELIABLE=1  > ./generated/lann.4.dve
divine combine -o ./lann/lann.mdve CR=1 N=6 PRECEDENCE=1 RELIABLE=1  > ./generated/lann.5.dve
divine combine -o ./lann/lann.mdve CR=0 N=4 PRECEDENCE=0 RELIABLE=0  > ./generated/lann.6.dve
divine combine -o ./lann/lann.mdve CR=1 N=5 PRECEDENCE=0 RELIABLE=0  > ./generated/lann.7.dve
divine combine -o ./lann/lann.mdve CR=0 N=5 PRECEDENCE=0 RELIABLE=0  > ./generated/lann.8.dve
divine combine -o ./leader_election/leader_election.mdve N=5  > ./generated/leader_election.1.dve
divine combine -o ./leader_election/leader_election.mdve  ERROR=1 N=5  > ./generated/leader_election.2.dve
divine combine -o ./leader_election/leader_election.mdve ERROR=2 N=6  > ./generated/leader_election.3.dve
divine combine -o ./leader_election/leader_election.mdve N=7  > ./generated/leader_election.4.dve
divine combine -o ./leader_election/leader_election.mdve N=8  > ./generated/leader_election.5.dve
divine combine -o ./leader_election/leader_election.mdve N=9  > ./generated/leader_election.6.dve
divine combine -o ./leader_filters/leader_filters.mdve N=3  > ./generated/leader_filters.1.dve
divine combine -o ./leader_filters/leader_filters.mdve ERROR=1 N=3  > ./generated/leader_filters.2.dve
divine combine -o ./leader_filters/leader_filters.mdve N=4  > ./generated/leader_filters.3.dve
divine combine -o ./leader_filters/leader_filters.mdve ERROR=2 N=4  > ./generated/leader_filters.4.dve
divine combine -o ./leader_filters/leader_filters.mdve N=5  > ./generated/leader_filters.5.dve
divine combine -o ./leader_filters/leader_filters.mdve ERROR=1 N=5  > ./generated/leader_filters.6.dve
divine combine -o ./leader_filters/leader_filters.mdve N=6  > ./generated/leader_filters.7.dve
divine combine -o ./lifts/lifts.mdve ENV=1 N=2  > ./generated/lifts.1.dve
divine combine -o ./lifts/lifts.mdve ENV=2 N=2  > ./generated/lifts.2.dve
divine combine -o ./lifts/lifts.mdve ENV=0 N=3  > ./generated/lifts.3.dve
divine combine -o ./lifts/lifts.mdve ENV=1 N=3  > ./generated/lifts.4.dve
divine combine -o ./lifts/lifts.mdve ENV=2 N=3  > ./generated/lifts.5.dve
divine combine -o ./lifts/lifts.mdve ENV=0 N=4  > ./generated/lifts.6.dve
divine combine -o ./lifts/lifts.mdve ENV=1 N=4  > ./generated/lifts.7.dve
divine combine -o ./lifts/lifts.mdve ENV=2 N=4  > ./generated/lifts.8.dve
divine combine -o ./loyd/loyd.mdve COLS=2  ROWS=3  > ./generated/loyd.1.dve
divine combine -o ./loyd/loyd.mdve COLS=3  ROWS=3  > ./generated/loyd.2.dve
divine combine -o ./loyd/loyd.mdve ROWS=3 COLS=4  > ./generated/loyd.3.dve
divine combine -o ./lup/lup.mdve N=4  > ./generated/lup.1.dve
divine combine -o ./lup/lup.mdve N=8  > ./generated/lup.2.dve
divine combine -o ./lup/lup.mdve N=9  > ./generated/lup.3.dve
divine combine -o ./lup/lup.mdve N=10  > ./generated/lup.4.dve
divine combine -o ./mcs/mcs.mdve N=3  > ./generated/mcs.1.dve
divine combine -o ./mcs/mcs.mdve ERROR=2 N=3  > ./generated/mcs.2.dve
divine combine -o ./mcs/mcs.mdve N=4  > ./generated/mcs.3.dve
divine combine -o ./mcs/mcs.mdve ERROR=1 N=4  > ./generated/mcs.4.dve
divine combine -o ./mcs/mcs.mdve N=5  > ./generated/mcs.5.dve
divine combine -o ./mcs/mcs.mdve ERROR=1 N=5  > ./generated/mcs.6.dve
divine combine -o ./msmie/msmie.mdve S=2 M=3 N=3  > ./generated/msmie.1.dve
divine combine -o ./msmie/msmie.mdve S=7 M=6 N=2  > ./generated/msmie.2.dve
divine combine -o ./msmie/msmie.mdve S=7 M=6 N=4  > ./generated/msmie.3.dve
divine combine -o ./msmie/msmie.mdve S=10 M=10 N=5  > ./generated/msmie.4.dve
divine combine -o ./needham/needham.mdve  R=1 I=1  > ./generated/needham.1.dve
divine combine -o ./needham/needham.mdve  R=2 I=2  > ./generated/needham.2.dve
divine combine -o ./needham/needham.mdve  R=2 I=3  > ./generated/needham.3.dve
divine combine -o ./needham/needham.mdve  R=3 I=3  > ./generated/needham.4.dve
divine combine -o ./peg_solitaire/peg_solitaire.mdve VERSION=1 CROSSWAYS=1 N=4  > ./generated/peg_solitaire.1.dve
divine combine -o ./peg_solitaire/peg_solitaire.mdve VERSION=2 CROSSWAYS=0  > ./generated/peg_solitaire.2.dve
divine combine -o ./peg_solitaire/peg_solitaire.mdve VERSION=2 CROSSWAYS=1  > ./generated/peg_solitaire.3.dve
divine combine -o ./peg_solitaire/peg_solitaire.mdve VERSION=1 CROSSWAYS=0 N=5  > ./generated/peg_solitaire.4.dve
divine combine -o ./peg_solitaire/peg_solitaire.mdve VERSION=3 CROSSWAYS=0  > ./generated/peg_solitaire.5.dve
divine combine -o ./peg_solitaire/peg_solitaire.mdve VERSION=1 CROSSWAYS=0 N=6  > ./generated/peg_solitaire.6.dve
divine combine -o ./peterson/peterson.mdve N=3  > ./generated/peterson.1.dve
divine combine -o ./peterson/peterson.mdve ERROR=1 N=3  > ./generated/peterson.2.dve
divine combine -o ./peterson/peterson.mdve ERROR=2 N=3  > ./generated/peterson.3.dve
divine combine -o ./peterson/peterson.mdve N=4  > ./generated/peterson.4.dve
divine combine -o ./peterson/peterson.mdve ERROR=1 N=4  > ./generated/peterson.5.dve
divine combine -o ./peterson/peterson.mdve ERROR=2 N=4  > ./generated/peterson.6.dve
divine combine -o ./peterson/peterson.mdve N=5  > ./generated/peterson.7.dve
divine combine -o ./pgm_protocol/pgm_protocol.mdve DATA_RCV=3  MAX_DATA=4 MAX_LOSS=3 TXW_SIZE=3  > ./generated/pgm_protocol.1.dve
divine combine -o ./pgm_protocol/pgm_protocol.mdve DATA_RCV=7 MAX_DATA=6 MAX_LOSS=1  > ./generated/pgm_protocol.2.dve
divine combine -o ./pgm_protocol/pgm_protocol.mdve DATA_RCV=9  MAX_DATA=9 MAX_LOSS=3 TXW_SIZE=4  > ./generated/pgm_protocol.3.dve
divine combine -o ./pgm_protocol/pgm_protocol.mdve DELAY=3 DATA_RCV=9  MAX_DATA=7 TXW_SIZE=4  > ./generated/pgm_protocol.4.dve
divine combine -o ./pgm_protocol/pgm_protocol.mdve DATA_RCV=9  MAX_DATA=9 DATA_PERIOD=3 MAX_LOSS=3  > ./generated/pgm_protocol.5.dve
divine combine -o ./pgm_protocol/pgm_protocol.mdve  DATA_PER_SPM=2 DATA_RCV=9 MAX_DATA=9 DATA_PERIOD=3 MAX_LOSS=4 TXW_SIZE=5  > ./generated/pgm_protocol.6.dve
divine combine -o ./pgm_protocol/pgm_protocol.mdve  DATA_PER_SPM=2 DATA_RCV=12  MAX_DATA=9  DATA_PERIOD=4 TXW_SIZE=6  > ./generated/pgm_protocol.7.dve
divine combine -o ./pgm_protocol/pgm_protocol.mdve DELAY=3 DATA_RCV=9 MAX_DATA=12 DATA_PERIOD=5 MAX_LOSS=5 TXW_SIZE=5  > ./generated/pgm_protocol.8.dve
divine combine -o ./phils/phils.mdve N=4  > ./generated/phils.1.dve
divine combine -o ./phils/phils.mdve ROOM=3 N=5  > ./generated/phils.2.dve
divine combine -o ./phils/phils.mdve N=6 LEFT=1  > ./generated/phils.3.dve
divine combine -o ./phils/phils.mdve ROOM=7 N=9  > ./generated/phils.4.dve
divine combine -o ./phils/phils.mdve N=12  > ./generated/phils.5.dve
divine combine -o ./phils/phils.mdve N=15  > ./generated/phils.6.dve
divine combine -o ./phils/phils.mdve ROOM=9 N=13  > ./generated/phils.7.dve
divine combine -o ./phils/phils.mdve N=16  > ./generated/phils.8.dve
divine combine -o ./plc/plc.mdve MAXTIME=100  > ./generated/plc.1.dve
divine combine -o ./plc/plc.mdve MAXTIME=300  > ./generated/plc.2.dve
divine combine -o ./plc/plc.mdve MAXTIME=500  > ./generated/plc.3.dve
divine combine -o ./plc/plc.mdve MAXTIME=600  > ./generated/plc.4.dve
divine combine -o ./pouring/pouring.mdve VERSION=2 X=4  > ./generated/pouring.1.dve
divine combine -o ./pouring/pouring.mdve VERSION=4 X=4  > ./generated/pouring.2.dve
divine combine -o ./production_cell/production_cell.mdve MAX=0 N=3  > ./generated/production_cell.1.dve
divine combine -o ./production_cell/production_cell.mdve MAX=5 N=2  > ./generated/production_cell.2.dve
divine combine -o ./production_cell/production_cell.mdve MAX=0 N=6  > ./generated/production_cell.3.dve
divine combine -o ./production_cell/production_cell.mdve MAX=4 N=4  > ./generated/production_cell.4.dve
divine combine -o ./production_cell/production_cell.mdve MAX=0 N=8  > ./generated/production_cell.5.dve
divine combine -o ./production_cell/production_cell.mdve MAX=0 N=10  > ./generated/production_cell.6.dve
divine combine -o ./protocols/protocols.mdve B=3 Strategy=0  > ./generated/protocols.1.dve
divine combine -o ./protocols/protocols.mdve Strategy=1  > ./generated/protocols.2.dve
divine combine -o ./protocols/protocols.mdve MAX=5 B=0 Strategy=2  > ./generated/protocols.3.dve
divine combine -o ./protocols/protocols.mdve MAX=30 B=20 Strategy=2  > ./generated/protocols.4.dve
divine combine -o ./protocols/protocols.mdve MAX=40 B=40 Strategy=2  > ./generated/protocols.5.dve
divine combine -o ./public_subscribe/public_subscribe.mdve  numFiles=3 numUsers=1  > ./generated/public_subscribe.1.dve
divine combine -o ./public_subscribe/public_subscribe.mdve  numFiles=1 numUsers=2  > ./generated/public_subscribe.2.dve
divine combine -o ./public_subscribe/public_subscribe.mdve  numFiles=2 numUsers=2  > ./generated/public_subscribe.3.dve
divine combine -o ./public_subscribe/public_subscribe.mdve  numFiles=3 numUsers=2  > ./generated/public_subscribe.4.dve
divine combine -o ./public_subscribe/public_subscribe.mdve  numFiles=1 numUsers=3  > ./generated/public_subscribe.5.dve
divine combine -o ./reader_writer/reader_writer.mdve W=9 R=7 ERROR=1  > ./generated/reader_writer.1.dve
divine combine -o ./reader_writer/reader_writer.mdve W=7 R=12  > ./generated/reader_writer.2.dve
divine combine -o ./reader_writer/reader_writer.mdve W=14 R=14 ERROR=1  > ./generated/reader_writer.3.dve
divine combine -o ./resistance/resistance.mdve N=1  > ./generated/resistance.1.dve
divine combine -o ./resistance/resistance.mdve N=2  > ./generated/resistance.2.dve
divine combine -o ./rether/rether.mdve Slots=5 N=3 RT_Slots=3  > ./generated/rether.1.dve
divine combine -o ./rether/rether.mdve Slots=4 N=4 RT_Slots=2  > ./generated/rether.2.dve
divine combine -o ./rether/rether.mdve Slots=3 N=7 RT_Slots=2  > ./generated/rether.3.dve
divine combine -o ./rether/rether.mdve ERROR=1 Slots=5 N=9 RT_Slots=3  > ./generated/rether.4.dve
divine combine -o ./rether/rether.mdve Slots=4 N=10 RT_Slots=3  > ./generated/rether.5.dve
divine combine -o ./rether/rether.mdve ERROR=1 Slots=5 N=11 RT_Slots=2  > ./generated/rether.6.dve
divine combine -o ./rether/rether.mdve Slots=6 N=12 RT_Slots=3  > ./generated/rether.7.dve
divine combine -o ./rushhour/rushhour.mdve VERSION=2  > ./generated/rushhour.1.dve
divine combine -o ./rushhour/rushhour.mdve VERSION=3  > ./generated/rushhour.2.dve
divine combine -o ./rushhour/rushhour.mdve VERSION=4  > ./generated/rushhour.3.dve
divine combine -o ./rushhour/rushhour.mdve VERSION=5  > ./generated/rushhour.4.dve
divine combine -o ./schedule_world/schedule_world.mdve VERSION=1  > ./generated/schedule_world.1.dve
divine combine -o ./schedule_world/schedule_world.mdve VERSION=3  > ./generated/schedule_world.2.dve
divine combine -o ./schedule_world/schedule_world.mdve VERSION=2  > ./generated/schedule_world.3.dve
divine combine -o ./sokoban/sokoban.mdve VERSION=3  > ./generated/sokoban.1.dve
divine combine -o ./sokoban/sokoban.mdve VERSION=4  > ./generated/sokoban.2.dve
divine combine -o ./sokoban/sokoban.mdve VERSION=2  > ./generated/sokoban.3.dve
divine combine -o ./sorter/sorter.mdve SCENARIO=1  > ./generated/sorter.1.dve
divine combine -o ./sorter/sorter.mdve SCENARIO=2  > ./generated/sorter.2.dve
divine combine -o ./sorter/sorter.mdve SCENARIO=3  > ./generated/sorter.3.dve
divine combine -o ./sorter/sorter.mdve SCENARIO=4  > ./generated/sorter.4.dve
divine combine -o ./sorter/sorter.mdve SCENARIO=5  > ./generated/sorter.5.dve
divine combine -o ./synapse/synapse.mdve Lines=2 ERROR=1  > ./generated/synapse.1.dve
divine combine -o ./synapse/synapse.mdve Lines=3  > ./generated/synapse.2.dve
divine combine -o ./synapse/synapse.mdve Lines=4  > ./generated/synapse.3.dve
divine combine -o ./synapse/synapse.mdve Lines=5  > ./generated/synapse.4.dve
divine combine -o ./synapse/synapse.mdve Lines=1 ERROR=1 N=3  > ./generated/synapse.5.dve
divine combine -o ./synapse/synapse.mdve Lines=2 N=3  > ./generated/synapse.6.dve
divine combine -o ./synapse/synapse.mdve Lines=3 N=3  > ./generated/synapse.7.dve
divine combine -o ./szymanski/szymanski.mdve N=3  > ./generated/szymanski.1.dve
divine combine -o ./szymanski/szymanski.mdve ERROR=1 N=3  > ./generated/szymanski.2.dve
divine combine -o ./szymanski/szymanski.mdve N=4  > ./generated/szymanski.3.dve
divine combine -o ./szymanski/szymanski.mdve ERROR=1 N=4  > ./generated/szymanski.4.dve
divine combine -o ./szymanski/szymanski.mdve N=5  > ./generated/szymanski.5.dve
divine combine -o ./telephony/telephony.mdve BACK=1 FORWARD=1 ERROR=1 N=2  > ./generated/telephony.1.dve
divine combine -o ./telephony/telephony.mdve BACK=0 FORWARD=0 ERROR=0 N=3  > ./generated/telephony.2.dve
divine combine -o ./telephony/telephony.mdve BACK=1 FORWARD=1 ERROR=1 N=3  > ./generated/telephony.3.dve
divine combine -o ./telephony/telephony.mdve BACK=0 FORWARD=0 ERROR=0 N=4  > ./generated/telephony.4.dve
divine combine -o ./telephony/telephony.mdve BACK=1 FORWARD=1 ERROR=0 N=4  > ./generated/telephony.5.dve
divine combine -o ./telephony/telephony.mdve BACK=1 FORWARD=0 ERROR=1 N=4  > ./generated/telephony.6.dve
divine combine -o ./telephony/telephony.mdve BACK=0 FORWARD=1 ERROR=1 N=4  > ./generated/telephony.7.dve
divine combine -o ./telephony/telephony.mdve BACK=1 FORWARD=1 ERROR=1 N=4  > ./generated/telephony.8.dve
divine combine -o ./train-gate/train-gate.mdve ERROR=1 N=3  > ./generated/train-gate.1.dve
divine combine -o ./train-gate/train-gate.mdve N=5  > ./generated/train-gate.2.dve
divine combine -o ./train-gate/train-gate.mdve ERROR=1 N=5  > ./generated/train-gate.3.dve
divine combine -o ./train-gate/train-gate.mdve N=7  > ./generated/train-gate.4.dve
divine combine -o ./train-gate/train-gate.mdve ERROR=1 N=7  > ./generated/train-gate.5.dve
divine combine -o ./train-gate/train-gate.mdve N=8  > ./generated/train-gate.6.dve
divine combine -o ./train-gate/train-gate.mdve N=9  > ./generated/train-gate.7.dve

