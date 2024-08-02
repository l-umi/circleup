import { useEffect, useState } from "react";
import CourseBanner from "./CourseBanner";
import CourseInputArea from "../CourseManagementContent/CourseInputArea";
import CourseCurriculum from "./Content/CourseCurriculum";
import { CourseDetailInfo } from "../../types/CourseDetailInfo";
import { useUserStore } from "../../store/store";

interface CourseStatusBoardProps {
    flag: string,
    data: CourseDetailInfo,

    onNewMyCourse?: (course: CourseDetailInfo) => void,
}

const CourseStatusBoard = ({ flag, data, onNewMyCourse }: CourseStatusBoardProps) => {

    // <ToDo> - 수강자가 수강 진행 중인 경우의 상태, { 수강 신청 버튼 X & 라이브 참가 허용 }
    const { nickName } = useUserStore();
    const [isReady, setIsReady] = useState(false);
    const [myCourse, setMyCourse] = useState<CourseDetailInfo>({
        id: 0,
        courseName: '',
        imgUrl: '',
        imgData: null,
        instructorName: nickName,
        description: '',
        tags: [],
        curriculum: '',
        view: 0,
        price: 0,
    });

    const courseNavbar = ['소개', '커리큘럼', '공지사항', '코멘트']
    const [activeTab, setActiveTab] = useState<string>('소개');

    const handleTabClick = (NavbarName: string) => {
        setActiveTab(NavbarName);
    };

    const updateImg = (newImg: string) => {
        myCourse.imgUrl = newImg
        onNewMyCourse!(myCourse)
    }

    const updateImgData = (newFile: FileList) => {
        myCourse.imgData = newFile
        onNewMyCourse!(myCourse)
    }

    const updateTitle = (newTitle: string) => {
        myCourse.courseName = newTitle
        onNewMyCourse!(myCourse)
    }

    const updateTags = (newTags: string[]) => {
        myCourse.tags = newTags
        onNewMyCourse!(myCourse)
    }

    const updateDescription = (newContent: string) => {
        myCourse.description = newContent;
        onNewMyCourse!(myCourse)
    }

    useEffect(() => {

        if (data) {
            setMyCourse(data);
            setIsReady(true);
        }
    }, [data]);

    if (!isReady) {
        return <div>Loading....</div>;
    }

    else {
        return (
            <div
                className="
                basis-3/4 w-[70%] h-full
                bg-white border border-gray-200
                rounded-lg
                my-5 mx-3
                dark:bg-gray-800 dark:border-gray-700
            ">
                {/* Banner - Image, price, CourseName, tags 정보를 얻어오는 part */}
                {(
                    () => {

                        switch (flag) {
                            case "userDetail":
                            case "instructorDetail":
                                return <CourseBanner
                                    isCreate={false} isModified={false} isDetail={true}
                                    imgUrl={myCourse.imgUrl} courseName={myCourse.courseName}
                                    instructorName={myCourse.instructorName} tags={myCourse.tags}
                                    price={myCourse.price} courseId={data.id}
                                    onImg={() => { }} onTitle={() => { }} onTags={() => { }}
                                />
                            case "instructorMake":
                                return <CourseBanner
                                    isCreate={true} isModified={false} isDetail={false}
                                    imgUrl={myCourse.imgUrl} courseName=""
                                    instructorName={myCourse.instructorName} tags={[]}
                                    price={0} courseId={data.id}
                                    onImg={updateImg} onImgData={updateImgData} onTitle={updateTitle} onTags={updateTags}
                                />
                            case "instructorModify":
                                return <CourseBanner
                                    isCreate={false} isModified={true} isDetail={false}
                                    imgUrl={myCourse.imgUrl} courseName={myCourse.courseName}
                                    instructorName={myCourse.instructorName} tags={myCourse.tags}
                                    price={myCourse.price} courseId={data.id}
                                    onImg={updateImg} onImgData={updateImgData} onTitle={updateTitle} onTags={updateTags}
                                />
                            default:
                                // Loading Spinner or other error pages
                                return <></>
                        }
                    }
                )()}
                <div className="
                w-auto h-auto
            ">
                    {/* 강의 Navbar */}
                    <div className="text-sm font-medium text-center text-gray-500 border-b border-gray-200 dark:text-gray-400 dark:border-gray-700 w-full mx-auto">
                        <ul className="mx-2 flex flex-wrap -mb-px">
                            {courseNavbar?.map((NavbarName, idx) => (
                                <li className="me-2" key={idx}>
                                    <a
                                        href="#"
                                        onClick={(e) => {
                                            e.preventDefault();
                                            handleTabClick(NavbarName);
                                        }}
                                        className={`inline-block p-4 border-b-2 rounded-t-lg text-sm ${activeTab === NavbarName
                                            ? 'text-blue-600 border-blue-600'
                                            : 'border-transparent hover:text-gray-600 hover:border-gray-300 dark:hover:text-gray-300'
                                            }`}
                                    >
                                        {NavbarName}
                                    </a>
                                </li>
                            ))}
                        </ul>
                    </div>
                    {/* select Main Content */}
                    {activeTab === '소개' ? (
                        () => {
                            switch (flag) {
                                case "instructorDetail":
                                    return (
                                        <blockquote className="text-base italic font-semibold text-gray-900 dark:text-white w-[80%] mx-auto mt-5 mb-8">
                                            <svg className="w-8 h-8 text-gray-400 dark:text-gray-600 mb-4" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 18 14">
                                                <path d="M6 0H2a2 2 0 0 0-2 2v4a2 2 0 0 0 2 2h4v1a3 3 0 0 1-3 3H2a1 1 0 0 0 0 2h1a5.006 5.006 0 0 0 5-5V2a2 2 0 0 0-2-2Zm10 0h-4a2 2 0 0 0-2 2v4a2 2 0 0 0 2 2h4v1a3 3 0 0 1-3 3h-1a1 1 0 0 0 0 2h1a5.006 5.006 0 0 0 5-5V2a2 2 0 0 0-2-2Z" />
                                            </svg>
                                            <p>"{myCourse.description}"</p>
                                        </blockquote>
                                    );
                                case "instructorMake":
                                    return <CourseInputArea
                                        original_content_title={""}
                                        original_content={""}
                                        onContent={updateDescription}
                                    />
                                case "instructorModify":
                                    return <CourseInputArea
                                        original_content_title={myCourse.courseName}
                                        original_content={myCourse.description}
                                        onContent={updateDescription}
                                    />
                                default:
                                    return <></>
                            }
                        }
                    )() : <></>}
                    {activeTab === '커리큘럼' ? <CourseCurriculum /> : <></>}
                    {activeTab === '공지사항' ? <div>공지사항 게시판 구현</div> : <></>}
                    {activeTab === '코멘트' ? <div>코멘트 게시판 구현</div> : <></>}
                </div>
            </div>);
    }
};

export default CourseStatusBoard;