//This program runs an ABM from the paper Racial Capitalism, section 5.2 (figure 6) , actors in two groups play a NDG
//it is assumed that there are two possible tags for powerless actors - one more dependable and one less
//the question is whether the powerful group comes to recognize the more dependable tag and use it to discriminate

public class Racism3 {

    public static void main(String[] args) {
            /*  try{
                FileWriter outfile = new FileWriter(args[0]);
                PrintWriter out = new PrintWriter(outfile);   */

        //Initialize parameters
        //agent num  = number of agents total, agentnumA+agentnumB = agentnum, stratnum=number of strategies
        //d = disagreement point for powerless agents (group A) and D for powerful ones (group B)
        //prob is the probability of the "powerless" tag being unreliable, i.e., seen as the powerful tag
        //mutate is the probability that each agent randomly mutates their strategy each round
        int agentnum = 40;
        int agentnumA= 20;
        int agentnumB = 20;
        int stratnum = 3;
        double d = 0;
        double D = 0.3;
        double prob=0.05;
        double mutate = 0.001;

        //xx is number of trials and xxx is length of each trial
        int xx = 2000;
        int xxx = 3000;

        //game played, H, M, L are high medium and low demands for NDG
        double H = .67;
        double M = .5;
        double L = .33;
        double[][] payoff0={{L,L,L},{M,M,d},{H,d,d}};
        double[][] payoff1={{L,L,L},{M,M,D},{H,D,D}};
        double diffA = H-d;
        double diffB = H-D;

        //variable to record data, outcomes is proportion of discriminatory interactions, reliable is the percentage of time powerful group is attending to the reliable tag, payoffP is avg payoff of group B, payoffW is avg payoff of group A
        double outcomes=0;
        double unreliable=0;
        double payoffP=0;
        double payoffW=0;

        //begin simulations, xx is the number of runs
        for (int ii = 0; ii < xx; ii++) {

            //intialize agents, entries track:
            //0 - group ID, 0 is group A (powerless), 1 is group B (powerful)
            // 1- strategy against group A, 0,1,or 2 for L, M, H
            //2 - strategy against group B, 0,1,or 2 for L, M, H
            //3 - strategy for tag recognition, 0 is reliable and 1 is less reliable
            int [][] agents = new int[agentnum][4];

            //randomly initialize strategies in 1 and 2
            for (int i = 0; i < agentnum; i++)
            {int dd = (int)(Math.random()*(stratnum));
                agents[i][1] = dd;}
            for (int i = 0; i < agentnum; i++)
            {int dd = (int)(Math.random()*(stratnum));
                agents[i][2] = dd;}
//initialize strategy for tag recognition, random for group B and all dependable for group A
            for (int i = 0; i < agentnum; i++)
            {int dd = (int)(Math.random()*(2));
                agents[i][3] = dd;
                agents[i][0] = 1;}
            for (int i = 0; i < agentnumA; i++)
            {agents[i][3] = 0;
                agents[i][0] = 0;}

            //matrix to record, for each agent, their payoff
            double []agentpayoff = new double[agentnum];

//begin runs, xxx is number of steps
            for (int iii = 0; iii < xxx; iii++) {

                //Each agent interacts with each other agent, calculate their payoffs
                //reset agent payoffs to 0
                for (int i = 0; i < agentnum; i++) {
                agentpayoff[i]=0;}
                //run through each pairing and assign payoffs for each interaction
                for (int i = 0; i < agentnum; i++) {
                    for (int j = i+1; j < agentnum; j++) {
                        //both group A
                        if(agents[i][0]== 0)
                            {if(agents[j][0]==0)
                            {double rr = payoff0[agents[i][1]][agents[j][1]];
                                agentpayoff[i] = agentpayoff[i]+rr;
                                double pp = payoff0[agents[j][1]][agents[i][1]];
                                agentpayoff[j] = agentpayoff[j]+pp;}
                            //group A, group B, and reliable
                            else if(agents[j][0]==1 && agents[j][3]==0)
                                    {double rr = payoff0[agents[i][2]][agents[j][1]];
                                    agentpayoff[i] = agentpayoff[i]+rr;
                                    double pp = payoff1[agents[j][1]][agents[i][2]];
                                    agentpayoff[j] = agentpayoff[j]+pp;}
                                        //group A, group B, and unreliable
                            else
                            {double uu = Math.random();
                                //if they get it wrong
                                if(uu<=prob)
                                {double rr = payoff0[agents[i][2]][agents[j][2]];
                                agentpayoff[i] = agentpayoff[i]+rr;
                                double pp = payoff1[agents[j][2]][agents[i][2]];
                                agentpayoff[j] = agentpayoff[j]+pp;}
                                //if they  get it right
                            else
                                {double rr = payoff0[agents[i][2]][agents[j][1]];
                                    agentpayoff[i] = agentpayoff[i]+rr;
                                    double pp = payoff1[agents[j][1]][agents[i][2]];
                                    agentpayoff[j] = agentpayoff[j]+pp;}}}
                                    //both in group B
                                 else
                            {double rr = payoff1[agents[i][2]][agents[j][2]];
                                agentpayoff[i] = agentpayoff[i]+rr;
                                double pp = payoff1[agents[j][2]][agents[i][2]];
                                agentpayoff[j] = agentpayoff[j]+pp;}}}
                //turn agent payoffs into averages
                for (int i = 0; i < agentnum; i++) {
                    agentpayoff[i]=agentpayoff[i]/(agentnum-1);}

//Now we do the imitation step, using pairwise proportional imitation dynamics, PPI
//
// for each agent, randomly match them with an agent who is in their in-group

                int b;
                for (int i = 0; i < agentnum; i++) {
                        do { b = (int) (Math.random() * (agentnum)); }
                        while (i == b || agents[i][0] != agents[b][0]);

                        //calculate probability of imitation as difference between their payoffs/total possible difference
                        double hhh = agentpayoff[b] - agentpayoff[i];
                    if(i<agentnumA)
                    {hhh= hhh/diffA;}
                    else
                    {hhh=hhh/diffB;}

                        //use random number to determine whether to imitate strategy
                        double ee = Math.random();
                        if (ee <= hhh) {
                            agents[i][1] = agents[b][1];
                            agents[i][2] = agents[b][2];
                            agents[i][3] = agents[b][3]; }

                    //use random number to decide whether to mutate, and, if so, mutate everything but true group
                    double jj = Math.random();
                    if (jj <= mutate) {
                        int tt = (int) (Math.random() * (stratnum));
                        agents[i][1] = tt;
                        int ss = (int) (Math.random() * (stratnum));
                        agents[i][2] = ss;
                        int dd = (int) (Math.random() * (2));
                        agents[i][3] = dd;}}

                //group A does not mutate reliable vs unreliable recognition
                for (int i = 0; i < agentnumA; i++) {
                    agents[i][3]=0;}

//end runs
            }

            //record outcomes - this tracks the proportion of interactions at the end of sim where we expect discrimination between groups
            double yy=0;
            for (int i = 0; i < agentnumA; i++) {
                for (int j = agentnumA; j < agentnum; j++)
                { if(agents[i][2]==0 && agents[j][1]==2)
                    {yy=yy+1;}
                    else if(agents[i][2]==2 && agents[j][1]==0)
                    {yy=yy+1;}}}
         yy = yy/(agentnumA*agentnumB);
            yy=yy/xx;
            outcomes = outcomes+yy;

            //record reliability - tracks how often the powerful group attends to the unreliable tag
            double ww=0;
                for (int j = agentnumA; j < agentnum; j++)
                { ww=ww+agents[j][3]; }
            ww = ww/agentnumB;
            ww=ww/xx;
            unreliable = unreliable+ww;

            //record payoffP and payoffW - this calculates avg payoff for powerful and powerless individuals at the end of simulation
 /*           double hh=0;
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
            payoffP=payoffP+hh; */


            //close simulations
        }

   //     System.out.println(outcomes);
     //   System.out.println(unreliable);
    //    System.out.println(payoffW);
      //  System.out.println(payoffP);

            /*     out.close();
        }
            catch (IOException ee)
            {ee.printStackTrace();}*/

        //out brackets
    }
}




