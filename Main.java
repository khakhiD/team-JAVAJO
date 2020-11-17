package Main;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main
{
	static Lecture [] getDatabase(String type, String userMajor, int userGrade, int userSemester, int userCredit)
	{
		int i = 0;
		Lecture [] lec = null;
		Connection conn = null;
		
		try
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
        	
			String url = "jdbc:mysql://localhost/test?characterEncoding=UTF-8&serverTimezone=UTC";
			//jdbc:mysql://~~~/디비이름, 물음표부터는 타임존 에러 방지용 문구. 만지면 큰일남.
        	
			conn = DriverManager.getConnection(url, "root", "1q2w3e4r!");
			//(, "이름", "비밀번호")
			System.out.println("<연결 성공>\n");
        	
			// 전공 필수 불러오기
			
			String query = "";
			if(type == "전공필수")
			{
				query = "select * from " + userMajor + "_" + userGrade + "학년_" + userSemester + "학기_전공필수";
			}
			else if(type == "전공선택")
			{
				query = "select * from " + userMajor + "_" + userGrade + "학년_" + userSemester + "학기_전공선택";
			}
			else if(type == "균형교양")
			{
				query = "select * from " + "균형_" + "1학년_공통";
			}
			
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);

			// 테이블 행의 길이
			int tableSize = 0;
			while(rs.next())
			{
				tableSize++;
			}
			
			// 전공 필수 목록 객체 생성
			lec = new Lecture[tableSize];	// 전공 필수 강의 DB
			
			rs = st.executeQuery(query);
			while(rs.next())
			{
				int id = rs.getInt("id");
				int grade = rs.getInt("grade");
				String name = rs.getString("classname");
				int credit = rs.getInt("score");
				int number_of_people = rs.getInt("personnumber");
				String professor = rs.getString("professor");
				String building = rs.getString("place");
				int classroom_1 = rs.getInt("cn1");
				int start_1 = rs.getInt("start1");
				int end_1 = rs.getInt("end1");
				int classroom_2 = rs.getInt("cn2");
				int start_2 = rs.getInt("start2");
				int end_2 = rs.getInt("end2");
				
				lec[i++] = new Lecture(name, credit, number_of_people, professor, building,
					classroom_1, start_1, end_1, classroom_2, start_2, end_2);
			}
        	
        	st.close();
        }
        catch(Exception e)
        {
        	System.err.println("속상하네");
			System.err.println(e.getMessage());
		}
		
		return lec;
	}
	
	// 재귀 함수와 조합 알고리즘을 통해 모든 시간표의 경우의 수 산출
	// n = 배열의 길이, r = 조합의 길이
	static void combination(Lecture[] lec, Schedule [] sche, boolean[] visited, int start, int n, int r, int classes)
    {
        if (r == 0)
        {
            addLecture(lec, sche, visited, n, classes);
            return;
        }

        for (int i = start; i < n; i++)
        {
            visited[i] = true;
            combination(lec, sche, visited, i + 1, n, r - 1, classes);
            visited[i] = false;
        }
    }

    // 강의 추가
    static void addLecture(Lecture[] lec, Schedule [] sche, boolean[] visited, int n, int classes)
    {
    	int j = 0;
    	
    	for (int i = 0; i < n; i++)
        {
        	for(int k = 0; k < sche[Schedule.num].size; k++)
        	{
        		// 시간표 안에 이름이 같은 강의(== 분반이 다른 같은 강의)가 있을 경우 시간표를 작성하지 않음
        		if(sche[Schedule.num].lectures[k].name == lec[i].name)
				{
					visited[i] = false;
				}
        		else	// 시간표에 이미 추가된 강의와 강의 시간이 겹치지 않는지 여부 판단
        		{
        			int x = 0;
        			int y = 0;
        			
        			int[] term_1 = new int[6];	// 시간표에 이미 추가된 강의의 강의 시간
					int[] term_2 = new int[6];	// 시간표에 추가 가능 여부를 판단하는 강의의 강의 시간
					
					for(x = sche[Schedule.num].lectures[k].start_1; x <= sche[Schedule.num].lectures[k].end_1; x++)
					{
						term_1[y] = x;
						y++;
					}
					
					// classroom_2 == 0일 경우 한 주에 두 번째 강의가 없음
					if(sche[Schedule.num].lectures[k].classroom_2 != 0)
					{
						for(x = sche[Schedule.num].lectures[k].start_2; x <= sche[Schedule.num].lectures[k].end_2; x++)
						{
							term_1[y] = x;
							y++;
						}
					}
					
					y = 0;
					for(x = lec[i].start_1; x <= lec[i].end_1; x++)
					{
						term_2[y] = x;
						y++;
					}
					
					if(lec[i].classroom_2 != 0)
					{
						for(x = lec[i].start_2; x <= lec[i].end_2; x++)
						{
							term_2[y] = x;
							y++;
						}
					}
					
					for(x = 0; x < term_1.length; x++)
					{
						for(y = 0; y < term_2.length; y++)
						{
							// term_1[x] == 0일 경우 해당 강의가 1시간, 또는 2시간 연강(== 3시간 연강이 아님)이기 때문에 그 시간이 겹치는지 확인할 필요가 없음
							if(term_1[x] == term_2[y] && term_1[x] != 0 && term_2[y] != 0)
							{
								visited[i] = false;
							}
						}
					}
        		}
			}
        		
        	if (visited[i])
            {
            	sche[Schedule.num].lectures[j].name = lec[i].name;
            	sche[Schedule.num].lectures[j].professor = lec[i].professor;
            	sche[Schedule.num].lectures[j].classroom_1 = lec[i].classroom_1;
            	sche[Schedule.num].lectures[j].start_1 = lec[i].start_1;
            	sche[Schedule.num].lectures[j].end_1 = lec[i].end_1;
            	sche[Schedule.num].lectures[j].classroom_2 = lec[i].classroom_2;
            	sche[Schedule.num].lectures[j].start_2 = lec[i].start_2;
            	sche[Schedule.num].lectures[j].end_2 = lec[i].end_2;
            	sche[Schedule.num].size++;
            	j++;
            }
        }
    	
    	// 완성된 시간표의 강의 개수가 입력된 강의 개수와 같다면 시간표의 possibility 멤버에 true 저장
    	if(sche[Schedule.num].size == classes)
    	{
    		sche[Schedule.num].possibility = true;
    	}
    	
    	// 시간표 순번 증감
        Schedule.num++;
    }
	
	public static void main(String [] args)
	{
		
		int i = 0, j = 0;
		int DB_Size = 0;			// 강의 DB의 크기
		int classes = 0;			// 시간표에 추가할 강의 개수
		String classCheck = "";		// 강의 이름을 통해 분반 여부를 확인할 변수
		
		String userMajor = "";		// 사용자의 학과
		int userGrade = 0;			// 사용자의 학년
		int userSemester = 0;		// 사용자의 학기
		int userCredit = 0;			// 사용자가 이수하고 하는 학점
		
		// 사용자 정보 입력
		Scanner scanner= new Scanner(System.in);
		System.out.print("사용자의 학과 : ");
		userMajor = scanner.next();
		System.out.print("사용자의 학년 : ");
		userGrade = scanner.nextInt();
		System.out.print("사용자의 학기 : ");
		userSemester = scanner.nextInt();
		System.out.print("사용자가 이수하고자 하는 학점 : ");
		userCredit = scanner.nextInt();
		
		Lecture [] requiredLec;		// 전공 필수 강의 DB
		Lecture [] selectionLec;	// 전공 선택 강의 DB
		Lecture [] liberal; 		// 교양 강의 DB
		requiredLec = getDatabase("전공필수", userMajor, userGrade, userSemester, userCredit);
		selectionLec = getDatabase("전공선택", userMajor, userGrade, userSemester, userCredit);
		liberal = getDatabase("균형교양", userMajor, userGrade, userSemester, userCredit);
		
		// 컴퓨터공학
		
		// 작성된 모든 시간표를 저장할 배열
		Schedule [] schedules;
		schedules = new Schedule[1000000];
		
		// 시간표 저장 배열의 객체 생성
		for(i = 0; i < schedules.length; i++)
		{
			schedules[i] = new Schedule();
		}
		
		// 전공 필수 DB 배열 + 전공 선택 DB 배열  + 교양 DB 배열
		Lecture [] userLec;
		userLec = new Lecture[requiredLec.length + selectionLec.length + liberal.length];
		System.arraycopy(requiredLec, 0, userLec, 0, requiredLec.length);  
		System.arraycopy(selectionLec, 0, userLec, requiredLec.length, selectionLec.length);
		System.arraycopy(liberal, 0, userLec, requiredLec.length + selectionLec.length, liberal.length);
        
		// 강의 이름을 통해 시간표에 추가할 강의 개수 산출
		classCheck = "";
		for(i = 0; i < userLec.length; i++)
		{
			if(!userLec[i].name.equals(classCheck))	// 강의 이름(classCheck)을 통해 분반 여부 검사
			{
				classes++;							// 강의 개수  증감
				classCheck = userLec[i].name;		// 강의 이름(classCheck) 업데이트
			}
		}
		
		DB_Size = userLec.length;
		boolean[] visited = new boolean[DB_Size];	// 경우의 수를 모두 구할 재귀 함수에 사용할 부울 배열
		combination(userLec, schedules, visited, 0, DB_Size, 6, 6);
		//combination(userLec, schedules, visited, 0, DB_Size, classes, classes);
        
		// 결과 출력
		for(i = 0; i < schedules.length; i++)
		{
			if(schedules[i].possibility)			// 완성된 시간표만 출력
			{
				System.out.println("시간표 " + i);
				for(j = 0; j < 10; j++)
				{
					System.out.print(schedules[i].lectures[j].name + " " + schedules[i].lectures[j].professor + " " + schedules[i].lectures[j].classroom_1 + " ");
					System.out.print(schedules[i].lectures[j].start_1 + " " + schedules[i].lectures[j].end_1 + " " + schedules[i].lectures[j].classroom_2 + " ");
					System.out.println(schedules[i].lectures[j].start_2 + " " + schedules[i].lectures[j].end_2);
				}
				System.out.println("");
			}
		}
	}
}
