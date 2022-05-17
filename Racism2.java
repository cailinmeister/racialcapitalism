//This program runs an ABM from the paper Racial Capitalism, section 5.1 (figures 4 and 5), actors in two groups play a NDG
//it is assumed that actors are more or less able to imitate those in the other group
//the question is whether this sort of flexibility prevents the emergence of inequity

public class Racism2 {
        public static void main(String[] args) {
            /*  try{
                FileWriter outfile = new FileWriter(args[0]);
                PrintWriter out = new PrintWriter(outfile);   */

            //Initialize parameters
            //agent num  = number of agents total, agentnumA+agentnumB = agentnum, stratnum=number of strategies
            //d = disagreement point for powerless agents (group A) and D for powerful ones (group B)
            //prob is the probability of imitating an outgroup member rather than an ingroup member
            //mutate is the probability that each agent randomly mutates their strategy each round
            int agentnum = 40;
            int agentnumA= 20;
            int agentnumB = 20;
            int stratnum = 3;
            double d = 0;
            double D = 0.32;
            double prob=0.01;
            double mutate = 0.0;
            //xx is number of trials and xxx is length of each trial
            int xx =2000;
            int xxx = 1000;

            //game played, H, M, L are high medium and low demands for NDG
            double H = .67;
            double M = .5;
            double L = .33;
            double[][] payoff0={{L,L,L},{M,M,d},{H,d,d}};
            double[][] payoff1={{L,L,L},{M,M,D},{H,D,D}};
            double diffA = H-d;
            double diffB = H-D;

            //variable to record data, outcomes is proportion of discriminatory interactions, outcomes2 is another measure, payoffP is avg payoff of group B, payoffW is avg payoff of group A
           //ID is used to track percentage of agents with "powerful" tag
            double outcomes=0;
            double outcomes2=0;
            double payoffP=0;
            double payoffW=0;
            double ID=0;


            //begin simulations, xx is the number of runs
            for (int ii = 0; ii < xx; ii++) {

                //intialize agents, entries track:
                //0 - self ID for tags, either 0 for a group A like tag, or 1 for a group B like tag, start with proper tags
                //1 - strategy against group A, 0,1,or 2 for L, M, H
                //2 - strategy against group B, 0,1,or 2 for L, M, H
                //3 - true group ID, 0 for group A and 1 for group B, does not change
                int [][] agents;
                agents = new int[agentnum][4];
                //initialize  entry
                for (int i = 0; i < agentnum; i++)
                {agents[i][0] = 1;
                    agents[i][3] = 1;}
                for (int i = 0; i < agentnumA; i++)
                {agents[i][0] = 0;
                    agents[i][3] = 0;}
                //randomly initialize strategies in 1 and 2
                for (int i = 0; i < agentnum; i++)
                {int dd = (int)(Math.random()*(stratnum));
                    agents[i][1] = dd;}
                for (int i = 0; i < agentnum; i++)
                {int dd = (int)(Math.random()*(stratnum));
                    agents[i][2] = dd;}

                //matrix to record, for each agent, their payoff
                double []agentpayoff = new double[agentnum];

//begin runs, xxx is number of steps
                for (int iii = 0; iii < xxx; iii++) {

                    //Each agent interacts with each other agent, calculate their payoffs
                    //start with group A
                    for (int i = 0; i < agentnumA; i++) {
                        double qq=0;
                        for (int j = 0; j < agentnum; j++) {
                            if(i==j) {}
                            else {
                                if(agents[i][0]== 0)
                                {if(agents[j][0]== 0)
                                {double rr = payoff0[agents[i][1]][agents[j][1]];
                                    qq = qq+rr;}
                                    if(agents[j][0]== 1)
                                    {double rr = payoff0[agents[i][2]][agents[j][1]];
                                        qq = qq+rr;}}
                                if(agents[i][0]== 1)
                                {if(agents[j][0]== 0)
                                {double rr = payoff0[agents[i][1]][agents[j][2]];
                                    qq = qq+rr;}
                                    if(agents[j][0]== 1)
                                    {double rr = payoff0[agents[i][2]][agents[j][2]];
                                        qq = qq+rr;}} } }
                        qq = qq/(agentnum-1);
                        agentpayoff[i]=qq;}

                    //do the same for group B
                    for (int i = agentnumA; i < agentnum; i++) {
                        double qq=0;
                        for (int j = 0; j < agentnum; j++) {
                            if(i==j) {}
                            else {
                                if(agents[i][0]== 0)
                                {if(agents[j][0]== 0)
                                {double rr = payoff1[agents[i][1]][agents[j][1]];
                                    qq = qq+rr;}
                                    if(agents[j][0]== 1)
                                    {double rr = payoff1[agents[i][2]][agents[j][1]];
                                        qq = qq+rr;}}
                                if(agents[i][0]== 1)
                                {if(agents[j][0]== 0)
                                {double rr = payoff1[agents[i][1]][agents[j][2]];
                                    qq = qq+rr;}
                                    if(agents[j][0]== 1)
                                    {double rr = payoff1[agents[i][2]][agents[j][2]];
                                        qq = qq+rr;}} } }
                        qq = qq/(agentnum-1);
                        agentpayoff[i]=qq;}

//Now we do the imitation step, using PPI, pairwise proportional imitation dynamics
                    //for each agent, randomly match them with an agent who is in their true in-group with 1-prob, and outgroup with prob

                    int b;
                    for (int i = 0; i < agentnum; i++) {
                        double rr = Math.random();

                        if (rr <= prob)
                        //pair with outgroup
                        { do {
                                b = (int) (Math.random() * (agentnum)); }
                            while (i == b || agents[i][3] == agents[b][3]);

                            //calculate probability of imitation as difference between their payoffs/total possible difference
                            double hhh = agentpayoff[b] - agentpayoff[i];
                            hhh = hhh / diffA;

                            //use random number to determine whether to imitate strategy
                            double ee = Math.random();
                            if (ee <= hhh) {
                                agents[i][0] = agents[b][0];
                                agents[i][1] = agents[b][1];
                                agents[i][2] = agents[b][2];
                            }
                        }
                        //pair with ingroup
                        else {
                            do { b = (int) (Math.random() * (agentnum)); }
                            while (i == b || agents[i][3] != agents[b][3]);

                            //calculate probability of imitation as difference between their payoffs/total possible difference
                            double hhh = agentpayoff[b] - agentpayoff[i];
                            if(i<agentnumA)
                            {hhh= hhh/diffA;}
                            else
                            {hhh=hhh/diffB;}

                            //use random number to determine whether to imitate strategy
                            double ee = Math.random();
                            if (ee <= hhh) {
                                agents[i][0] = agents[b][0];
                                agents[i][1] = agents[b][1];
                                agents[i][2] = agents[b][2];
                            }
                        }

                        //use random number to decide whether to mutate, and, if so, mutate everything but true group
                        double jj = Math.random();
                        if (jj <= mutate) {
                            int dd = (int) (Math.random() * (2));
                            agents[i][0] = dd;
                            int tt = (int) (Math.random() * (stratnum));
                            agents[i][1] = tt;
                            int ss = (int) (Math.random() * (stratnum));
                            agents[i][2] = ss;
                        }

                    }
//end runs
                }

                //record outcomes - this tracks the proportion of interactions at the end of sim where we expect discrimination
                double yy=0;
                double uu=0;
                for (int i = 0; i < agentnum; i++) {
                    for (int j = 0; j < agentnum; j++)
                    { if(i==j)
                    {}
                    else if(agents[i][0]!=agents[j][0])
                    {uu=uu+1;
                        if(agents[i][0]==0 && agents[i][2]==2)
                        {yy=yy+1;}
                        else if(agents[i][0]==1 && agents[i][1]==2)
                        {yy=yy+1;}
                        else
                        {} }}}
                if(uu==0)
                {yy=0;}
                else
                { yy = yy/uu;}
                yy=yy/xx;
                outcomes = outcomes+yy;

                //record second discrimination measure

                yy=0;
                for (int i = 0; i < agentnum; i++) {
                    for (int j =0; j < agentnum; j++)
                    {if(i==j)
                    {}
                    else if(agents[i][0]!=agents[j][0])
                    {if(agents[i][0]==0 && agents[i][2]==2)
                    {yy=yy+1;}
                    else if(agents[i][0]==1 && agents[i][1]==2)
                    {yy=yy+1;}
                    }
                    }}
                uu=agentnum*(agentnum-1);
                yy = yy/uu;
                yy=yy/xx;
                outcomes2 = outcomes2+yy;

                //record payoffP and payoffW - this calculates avg payoff for powerful and powerless individuals at the end of simulation
                double hh=0;
                //powerless first
                for (int i = 0; i < agentnumA; i++) {
                    for (int j = agentnumA; j < agentnum; j++)
                    { if(agents[i][0]==0)
                    { if(agents[j][0]==0)
                    {hh=hh+payoff0[agents[i][1]][agents[j][1]];}
                    else if(agents[j][0]==1)
                    {hh=hh+payoff0[agents[i][2]][agents[j][1]];} }
                    else if(agents[i][0]==1)
                    { if(agents[j][0]==0)
                    {hh=hh+payoff0[agents[i][1]][agents[j][2]];}
                    else if(agents[j][0]==1)
                    {hh=hh+payoff0[agents[i][2]][agents[j][2]];} }
                    }}
                hh=hh/(agentnumA*agentnumB);
                hh=hh/xx;
                payoffW=payoffW+hh;

                //powerful next
                hh=0;
                for (int i = agentnumA; i < agentnum; i++) {
                    for (int j = 0; j < agentnumA; j++)
                    { if(agents[i][0]==0)
                    { if(agents[j][0]==0)
                    {hh=hh+payoff1[agents[i][1]][agents[j][1]];}
                    else if(agents[j][0]==1)
                    {hh=hh+payoff1[agents[i][2]][agents[j][1]];} }
                    else if(agents[i][0]==1)
                    { if(agents[j][0]==0)
                    {hh=hh+payoff1[agents[i][1]][agents[j][2]];}
                    else if(agents[j][0]==1)
                    {hh=hh+payoff1[agents[i][2]][agents[j][2]];} }
                    }}
                hh=hh/(agentnumB*agentnumA);
                hh=hh/xx;
                payoffP=payoffP+hh;

                //record percentage of agents with the "powerful" tag
                double power=0;
                for (int i = 0; i < agentnum; i++)
                {power= power+agents[i][0];}
                power=power/agentnum;
                power=power/xx;
                ID=ID+power;


                //close simulations
            }

            System.out.println(outcomes);
            System.out.println(outcomes2);
            System.out.println(payoffW);
            System.out.println(payoffP);
            System.out.println(ID);
            /*     out.close();
        }
            catch (IOException ee)
            {ee.printStackTrace();}*/

            //out brackets
        }
    }


