byte fork[6];


active proctype phil_0() { 

think: if
::  d_step {fork[1]==0;fork[1] = 1;}  goto one; 

fi;
one: if
::  d_step {fork[0]==0;fork[0] = 1;}  goto eat; 

fi;
eat: if
:: fork[1] = 0; goto finish; 

fi;
finish: if
:: fork[0] = 0; goto think; 

fi;
}

active proctype phil_1() { 

think: if
::  d_step {fork[1]==0;fork[1] = 1;}  goto one; 

fi;
one: if
::  d_step {fork[2]==0;fork[2] = 1;}  goto eat; 

fi;
eat: if
:: fork[1] = 0; goto finish; 

fi;
finish: if
:: fork[2] = 0; goto think; 

fi;
}

active proctype phil_2() { 

think: if
::  d_step {fork[2]==0;fork[2] = 1;}  goto one; 

fi;
one: if
::  d_step {fork[3]==0;fork[3] = 1;}  goto eat; 

fi;
eat: if
:: fork[2] = 0; goto finish; 

fi;
finish: if
:: fork[3] = 0; goto think; 

fi;
}

active proctype phil_3() { 

think: if
::  d_step {fork[3]==0;fork[3] = 1;}  goto one; 

fi;
one: if
::  d_step {fork[4]==0;fork[4] = 1;}  goto eat; 

fi;
eat: if
:: fork[3] = 0; goto finish; 

fi;
finish: if
:: fork[4] = 0; goto think; 

fi;
}

active proctype phil_4() { 

think: if
::  d_step {fork[4]==0;fork[4] = 1;}  goto one; 

fi;
one: if
::  d_step {fork[5]==0;fork[5] = 1;}  goto eat; 

fi;
eat: if
:: fork[4] = 0; goto finish; 

fi;
finish: if
:: fork[5] = 0; goto think; 

fi;
}

active proctype phil_5() { 

think: if
::  d_step {fork[5]==0;fork[5] = 1;}  goto one; 

fi;
one: if
::  d_step {fork[0]==0;fork[0] = 1;}  goto eat; 

fi;
eat: if
:: fork[5] = 0; goto finish; 

fi;
finish: if
:: fork[0] = 0; goto think; 

fi;
}

