
import { useEffect, useState } from "react";
import { getUserCourse } from "../../services/api";
import MyCourseList from "../../components/List/MyCourseList";
import MemberModfiyModal from "../../components/Modal/MemeberModfiyModal";
import { CourseInfo } from "../../types/CourseInfo";

const MyPage = () => {

    const [myCourses, setMyCourse] = useState<CourseInfo[]>([]);
    const [showModal, setShowModal] = useState<boolean>(false);
    const [isTokenRefreshing, setIsTokenRefreshing] = useState<boolean>(false);

    const toggleModal = () => {
        setShowModal(!showModal);
    };

    const fetchUserCourses = async () => {
        return await getUserCourse();
    };

    const handleModfiyUser = () => {
        toggleModal();
    };

    const handleDeleteUser = () => {
        console.log("회원삭제");
    };
    // { 추가 구현 } : 기타 정보, 연속 수강 or 랭킹 정보..

    const fetchMyCourses = async () => {

        setIsTokenRefreshing(true);
        try {
            const response = await fetchUserCourses();
            setMyCourse(response.data);
        } catch (error) {
            console.error("Error fetching courses:", error);
        } finally {
            setIsTokenRefreshing(false);
        }
    };

    useEffect(() => {
        fetchMyCourses();
    }, []);

    if (myCourses.length === 0 && isTokenRefreshing) {
        return <div>Token 재갱신 중 || Loading...</div>;
    }

    return (
        <div>
            <MemberModfiyModal show={showModal} onClose={toggleModal} />
            <div className="flex flex-row">
                <div className="
                basis-4/5
                w-full
                h-dvh
                p-4 bg-white 
                border border-gray-200
                my-5 mx-3
                mx-auto
                sm:p-8 
                dark:bg-gray-800 dark:border-gray-700
            "
                >
                    <MyCourseList onMyPage={true} title={"수강 중인 강의"} myCourses={myCourses} />
                </div>
            </div>
            <div className="flex flex-row">
                <div className="flex basis-2/3 justify-end ml-10">
                    <button
                        type="button"
                        className="
                            text-white bg-blue-700 hover:bg-blue-800
                            focus:ring-4 focus:ring-blue-300 
                            font-medium 
                            rounded-lg 
                            text-sm 
                            px-5 py-2.5 me-2
                            dark:bg-blue-600 dark:hover:bg-blue-700 
                            focus:outline-none dark:focus:ring-blue-800            
                            "
                        onClick={handleModfiyUser}
                    >
                        회원 수정
                    </button>
                    <button
                        type="button"
                        className="
                            text-white bg-blue-700 hover:bg-blue-800
                            focus:ring-4 focus:ring-blue-300 
                            font-medium 
                            rounded-lg 
                            text-sm 
                            px-5 py-2.5
                            dark:bg-blue-600 dark:hover:bg-blue-700 
                            focus:outline-none dark:focus:ring-blue-800
                            "
                        onClick={handleDeleteUser}
                    >
                        회원 탈퇴
                    </button>
                </div>
            </div>
        </div>
    );
};

export default MyPage;
