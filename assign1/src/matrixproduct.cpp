#include <stdio.h>
#include <iostream>
#include <iomanip>
#include <time.h>
#include <cstdlib>
#include <papi.h>
#include <fstream>
#include <cmath>

using namespace std;

#define SYSTEMTIME clock_t
 
double OnMult(int m_ar, int m_br) 
{
	
	SYSTEMTIME Time1, Time2;
	
	char st[100];
	double temp;
	int i, j, k;

	double *pha, *phb, *phc;
		
    pha = (double *)malloc((m_ar * m_ar) * sizeof(double));
	phb = (double *)malloc((m_ar * m_ar) * sizeof(double));
	phc = (double *)malloc((m_ar * m_ar) * sizeof(double));

	for(i=0; i<m_ar; i++)
		for(j=0; j<m_ar; j++)
			
			pha[i*m_ar + j] = (double)1.0;



	for(i=0; i<m_br; i++)
		for(j=0; j<m_br; j++)
			phb[i*m_br + j] = (double)(i+1);



    Time1 = clock();

	for(i=0; i<m_ar; i++)
	{	for( j=0; j<m_br; j++)
		{	
			phc[i*m_ar+j] = 0;
			for( k=0; k<m_ar; k++)
			{	
				phc[i*m_ar+j] += pha[i*m_ar+k] * phb[k*m_br+j];
			}
		}
	}

    double elapsed_time = (double) (clock() - Time1) / CLOCKS_PER_SEC;
	sprintf(st, "Time: %3.3f seconds\n", elapsed_time);
	cout << st;

	// display 10 elements of the result matrix tto verify correctness
	cout << "Result matrix: " << endl;
	for(i=0; i<1; i++)
	{	for(j=0; j<min(10,m_br); j++)
			cout << phc[j] << " ";
	}
	cout << endl;

    free(pha);
    free(phb);
    free(phc);
	return elapsed_time;
}

// add code here for line x line matriz multiplication
double OnMultLine(int m_ar, int m_br)
{
    SYSTEMTIME Time1, Time2;
	
	char st[100];
	double temp;
	int i, j, k;

	double *pha, *phb, *phc;
		
    pha = (double *)malloc((m_ar * m_ar) * sizeof(double));
	phb = (double *)malloc((m_ar * m_ar) * sizeof(double));
	phc = (double *)malloc((m_ar * m_ar) * sizeof(double));

	for(i=0; i<m_ar; i++)
		for(j=0; j<m_ar; j++){
			pha[i*m_ar + j] = (double)1.0;
			phc[i*m_ar + j] = 0;
		}

	for(i=0; i<m_br; i++)
		for(j=0; j<m_br; j++)
			phb[i*m_br + j] = (double)(i+1);

    Time1 = clock();

	for(i=0; i<m_ar; i++)
	{	for( k=0; k<m_ar; k++)
		{	
			for( j=0; j<m_br; j++)
			{	
				phc[i*m_ar+j] += pha[i*m_ar+k] * phb[k*m_br+j];
			}
		}
	}


    double elapsed_time = (double) (clock() - Time1) / CLOCKS_PER_SEC;
	sprintf(st, "Time: %3.3f seconds\n", elapsed_time);
	cout << st;

	// display 10 elements of the result matrix tto verify correctness
	cout << "Result matrix: " << endl;
	for(i=0; i<1; i++)
	{	for(j=0; j<min(10,m_br); j++)
			cout << phc[j] << " ";
	}
	cout << endl;

    free(pha);
    free(phb);
    free(phc);
	return elapsed_time;
    
}

// add code here for block x block matriz multiplication
double OnMultBlock(int m_ar, int m_br, int bkSize)
{
    SYSTEMTIME Time1, Time2;
	
	char st[100];
	double temp;
	int i, j, k, l, m, n;

	double *pha, *phb, *phc;
		
    pha = (double *)malloc((m_ar * m_ar) * sizeof(double));
	phb = (double *)malloc((m_ar * m_ar) * sizeof(double));
	phc = (double *)malloc((m_ar * m_ar) * sizeof(double));

	for(i=0; i<m_ar; i++)
		for(j=0; j<m_ar; j++){
			pha[i*m_ar + j] = (double)1.0;
			phc[i*m_ar + j] = 0;
		}

	for(i=0; i<m_br; i++)
		for(j=0; j<m_br; j++)
			phb[i*m_br + j] = (double)(i+1);

    Time1 = clock();
	
	for(l=0; l < m_ar; l += bkSize) {
		for(m=0; m < m_ar; m += bkSize) {
			for(n=0; n < m_ar; n += bkSize) {
				for(i=l; i<min(l+bkSize, m_ar); i++) {	
					for( k=n; k<min(n+bkSize, m_ar); k++){	
						for( j=m; j<min(m+bkSize, m_ar); j++){	
							phc[i*m_ar + j] += pha[i*m_ar + k] * phb[k*m_br+j];
						}
					}
				}
			}
		}
	}

	double elapsed_time = (double) (clock() - Time1) / CLOCKS_PER_SEC;
	sprintf(st, "Time: %3.3f seconds\n", elapsed_time);
	cout << st;

	// display 10 elements of the result matrix tto verify correctness
	cout << "Result matrix: " << endl;
	for(i=0; i<1; i++)
	{	for(j=0; j<min(10,m_br); j++)
			cout << phc[j] << " ";
	}
	cout << endl;

    free(pha);
    free(phb);
    free(phc);
	return elapsed_time;
}


void handle_error (int retval)
{
  printf("PAPI error %d: %s\n", retval, PAPI_strerror(retval));
  exit(1);
}

void init_papi() {
  int retval = PAPI_library_init(PAPI_VER_CURRENT);
  if (retval != PAPI_VER_CURRENT && retval < 0) {
    printf("PAPI library version mismatch!\n");
    exit(1);
  }
  if (retval < 0) handle_error(retval);

  std::cout << "PAPI Version Number: MAJOR: " << PAPI_VERSION_MAJOR(retval)
            << " MINOR: " << PAPI_VERSION_MINOR(retval)
            << " REVISION: " << PAPI_VERSION_REVISION(retval) << "\n";
}

void get_results(){
	//PAPI
	int EventSet = PAPI_NULL;
  	int ret;
	
	ret = PAPI_library_init( PAPI_VER_CURRENT );
	if ( ret != PAPI_VER_CURRENT )
		std::cout << "FAIL" << endl;

	ret = PAPI_create_eventset(&EventSet);
	if (ret != PAPI_OK) cout << "ERROR: create eventset" << endl;
	
	ret = PAPI_add_event(EventSet,PAPI_L1_DCM ); //Not Derived
	if (ret != PAPI_OK) cout << "ERROR: PAPI_L1_DCM" << ret << endl;

	ret = PAPI_add_event(EventSet, PAPI_CA_SNP); //Not Derived
	if (ret != PAPI_OK) cout << "ERROR: PAPI_CA_SNP" << ret << endl;

	ret = PAPI_add_event(EventSet, PAPI_L3_LDM); //Not Derived
	if (ret != PAPI_OK) cout << "ERROR: PAPI_L3_LDM" << ret << endl;

	ret = PAPI_add_event(EventSet, PAPI_PRF_DM); //Not Derived
	if (ret != PAPI_OK) cout << "ERROR: PAPI_PRF_DM" << ret << endl;

	ret = PAPI_add_event(EventSet, PAPI_MEM_WCY); //Not Derived
	if (ret != PAPI_OK) cout << "ERROR: PAPI_MEM_WCY" << ret << endl;	

	ret = PAPI_add_event(EventSet, PAPI_L1_LDM); //Not Derived
	if (ret != PAPI_OK) cout << "ERROR: PAPI_L1_LDM" << ret << endl;

	ret = PAPI_add_event(EventSet, PAPI_L2_LDM); //Not Derived
	if (ret != PAPI_OK) cout << "ERROR: PAPI_L2_LDM" << ret << endl;

	/*ret = PAPI_add_event(EventSet,PAPI_L2_DCM); //Derived
	if (ret != PAPI_OK) cout << "ERROR: PAPI_L2_DCM" << ret << endl;

	ret = PAPI_add_event(EventSet, PAPI_TLB_DM); //Derived
	if (ret != PAPI_OK) cout << "ERROR: PAPI_TLB_DM" << ret << endl;

	ret = PAPI_add_event(EventSet,PAPI_L2_DCA); //Not Derived
	if (ret != PAPI_OK) cout << "ERROR: PAPI_L2_DCA" << ret << endl;

	ret = PAPI_add_event(EventSet,PAPI_L3_DCA); //Derived
	if (ret != PAPI_OK) cout << "ERROR: PAPI_L3_DCA" << ret << endl;*/

	//File
	fstream fout;
	fout.open("Results_256_block_c++.csv", ios::out | ios::trunc);

	//Normal Multiplication
	
	/*fout << "Matrix Size; Time; PAPI_L1_DCM; PAPI_CA_SNP; PAPI_L3_LDM; PAPI_PRF_DM; PAPI_MEM_WCY; PAPI_L1_LDM; PAPI_L2_LDM; \n";*/
	/*fout << "Matrix Size; Time; PAPI_L2_DCM; PAPI_TLB_DM; PAPI_L2_DCA; PAPI_L3_DCA\n";
	
	for(int i = 600; i <= 3000; i += 400){
		ret = PAPI_start(EventSet);
		if (ret != PAPI_OK) cout << "ERROR: Start PAPI" << endl;

		double time = OnMult(i, i);

		long long values[4];
		for(int i = 0; i < 4; i++) values[i] = 0;
		ret = PAPI_stop(EventSet, values);
		if (ret != PAPI_OK) cout << "ERROR: Stop PAPI" << endl;

		fout << i << "; " << time;
		for(int v = 0; v < 4; v++){
			fout << "; " << values[v];
		}
		fout << "\n";
		
		ret = PAPI_reset( EventSet );
		if ( ret != PAPI_OK )
			std::cout << "FAIL reset" << endl;
	}
	fout << "\n\n";*/
	
	//Line Multiplication
	/*fout << "Matrix Size; Time; PAPI_L1_DCM; PAPI_CA_SNP; PAPI_L3_LDM; PAPI_PRF_DM; PAPI_MEM_WCY; PAPI_L1_LDM; PAPI_L2_LDM; \n";*/
	/*fout << "Matrix Size; Time; PAPI_L2_DCM; PAPI_TLB_DM; PAPI_L2_DCA; PAPI_L3_DCA\n";

	for(int i = 600; i <= 3000; i += 400){
		ret = PAPI_start(EventSet);
		if (ret != PAPI_OK) cout << "ERROR: Start PAPI" << endl;

		double time = OnMultLine(i, i);

		long long values[4];
		for(int i = 0; i < 4; i++) values[i] = 0;
		ret = PAPI_stop(EventSet, values);
		if (ret != PAPI_OK) cout << "ERROR: Stop PAPI" << endl;

		fout << i << "; " << time;
		for(int v = 0; v < 4; v++){
			fout << "; " << values[v];
		}
		fout << "\n";

		ret = PAPI_reset( EventSet );
		if ( ret != PAPI_OK )
			std::cout << "FAIL reset" << endl;
	}
	for(int i = 4096; i <= 10240; i += 2048){
		ret = PAPI_start(EventSet);
		if (ret != PAPI_OK) cout << "ERROR: Start PAPI" << endl;

		double time = OnMultLine(i, i);

		long long values[4];
		for(int i = 0; i < 4; i++) values[i] = 0;
		ret = PAPI_stop(EventSet, values);
		if (ret != PAPI_OK) cout << "ERROR: Stop PAPI" << endl;

		fout << i << "; " << time;
		for(int v = 0; v < 4; v++){
			fout << "; " << values[v];
		}
		fout << "\n";

		ret = PAPI_reset( EventSet );
		if ( ret != PAPI_OK )
			std::cout << "FAIL reset" << endl;
	}
	fout << "\n\n";*/
	
	//Block Multiplication
	fout << "Matrix Size; Block Size; Time; PAPI_L1_DCM; PAPI_CA_SNP; PAPI_L3_LDM; PAPI_PRF_DM; PAPI_MEM_WCY; PAPI_L1_LDM; PAPI_L2_LDM; \n";
	/*fout << "Matrix Size; Block Size; Time; PAPI_L2_DCM; PAPI_TLB_DM; PAPI_L2_DCA; PAPI_L3_DCA\n";*/

	for(int i = 8192; i <= 8192; i += 2048){
		for(int j = 256; j <= 256; j *= 2){
			ret = PAPI_start(EventSet);
			if (ret != PAPI_OK) cout << "ERROR: Start PAPI" << endl;

			double time = OnMultBlock(i, i, j);

			long long values[7];
			for(int i = 0; i < 7; i++) values[i] = 0;
			ret = PAPI_stop(EventSet, values);
			if (ret != PAPI_OK) cout << "ERROR: Stop PAPI" << endl;

			fout << i << "; " << j << "; " << time;
			for(int v = 0; v < 7; v++){
				fout << "; " << values[v];
			}
			fout << "\n";

			ret = PAPI_reset( EventSet );
			if ( ret != PAPI_OK )
				std::cout << "FAIL reset" << endl;
		}
	}

	ret = PAPI_remove_event( EventSet, PAPI_L1_DCM );
	if ( ret != PAPI_OK )
		std::cout << "FAIL remove event" << endl; 

	ret = PAPI_remove_event( EventSet, PAPI_CA_SNP );
	if ( ret != PAPI_OK )
		std::cout << "FAIL remove event" << endl; 

	ret = PAPI_remove_event( EventSet, PAPI_L1_LDM );
	if ( ret != PAPI_OK )
		std::cout << "FAIL remove event" << endl; 

	ret = PAPI_remove_event( EventSet, PAPI_L2_LDM );
	if ( ret != PAPI_OK )
		std::cout << "FAIL remove event" << endl; 

	ret = PAPI_remove_event( EventSet, PAPI_L3_LDM );
	if ( ret != PAPI_OK )
		std::cout << "FAIL remove event" << endl;

	ret = PAPI_remove_event( EventSet, PAPI_PRF_DM );
	if ( ret != PAPI_OK )
		std::cout << "FAIL remove event" << endl; 

	ret = PAPI_remove_event( EventSet, PAPI_MEM_WCY );
	if ( ret != PAPI_OK )
		std::cout << "FAIL remove event" << endl;

	/*ret = PAPI_remove_event( EventSet, PAPI_L2_DCM );
	if ( ret != PAPI_OK )
		std::cout << "FAIL remove event" << endl; 

	ret = PAPI_remove_event( EventSet, PAPI_TLB_DM );
	if ( ret != PAPI_OK )
		std::cout << "FAIL remove event" << endl; 

	ret = PAPI_remove_event( EventSet, PAPI_L2_DCA );
	if ( ret != PAPI_OK )
		std::cout << "FAIL remove event" << endl;

	ret = PAPI_remove_event( EventSet, PAPI_L3_DCA );
	if ( ret != PAPI_OK )
		std::cout << "FAIL remove event" << endl;*/

	ret = PAPI_destroy_eventset( &EventSet );
	if ( ret != PAPI_OK )
		std::cout << "FAIL destroy" << endl;
}


int main (int argc, char *argv[])
{
	get_results();
	
	/*char c;
	int lin, col, blockSize;
	int op;
	
	int EventSet = PAPI_NULL;
  	long long values[2];
  	int ret;
	

	ret = PAPI_library_init( PAPI_VER_CURRENT );
	if ( ret != PAPI_VER_CURRENT )
		std::cout << "FAIL" << endl;


	ret = PAPI_create_eventset(&EventSet);
		if (ret != PAPI_OK) cout << "ERROR: create eventset" << endl;


	ret = PAPI_add_event(EventSet,PAPI_L1_DCM );
	if (ret != PAPI_OK) cout << "ERROR: PAPI_L1_DCM" << endl;


	ret = PAPI_add_event(EventSet,PAPI_L2_DCM);
	if (ret != PAPI_OK) cout << "ERROR: PAPI_L2_DCM" << endl;


	op=1;
	do {
		cout << endl << "1. Multiplication" << endl;
		cout << "2. Line Multiplication" << endl;
		cout << "3. Block Multiplication" << endl;
		cout << "4. Save testing results" << endl;
		cout << "Selection?: ";
		cin >>op;
		if (op == 0)
			break;
		printf("Dimensions: lins=cols ? ");
   		cin >> lin;
   		col = lin;


		// Start counting
		ret = PAPI_start(EventSet);
		if (ret != PAPI_OK) cout << "ERROR: Start PAPI" << endl;

		switch (op){
			case 1: 
				OnMult(lin, col);
				break;
			case 2:
				OnMultLine(lin, col);  
				break;
			case 3:
				cout << "Block Size? ";
				cin >> blockSize;
				OnMultBlock(lin, col, blockSize);  
				break;
			case 4:
				get_results();

		}

  		ret = PAPI_stop(EventSet, values);
  		if (ret != PAPI_OK) cout << "ERROR: Stop PAPI" << endl;
  		printf("L1 DCM: %lld \n",values[0]);
  		printf("L2 DCM: %lld \n",values[1]);

		ret = PAPI_reset( EventSet );
		if ( ret != PAPI_OK )
			std::cout << "FAIL reset" << endl; 



	}while (op != 0);

	ret = PAPI_remove_event( EventSet, PAPI_L1_DCM );
	if ( ret != PAPI_OK )
		std::cout << "FAIL remove event" << endl; 

	ret = PAPI_remove_event( EventSet, PAPI_L2_DCM );
	if ( ret != PAPI_OK )
		std::cout << "FAIL remove event" << endl; 

	ret = PAPI_destroy_eventset( &EventSet );
	if ( ret != PAPI_OK )
		std::cout << "FAIL destroy" << endl;

	
	*/
}