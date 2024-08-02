import { useNavigate } from "react-router-dom";
import { useUserStore } from "../../store/store";
import { CourseInfo } from "../../types/CourseInfo";

// props
interface MyCourseProps {
    data : CourseInfo
}

const MyCourse = ({ data } : MyCourseProps) => {

    const navigate = useNavigate();
    const { role } = useUserStore();
    const { imgUrl, id : courseId, name } = data;

    function navigateToMyCourseDetail() {
        if(role === 'Instructor')
            navigate("/courseManagementDetail", { state: { courseId: courseId } });
        else
            navigate('/detailCourse', {state: { courseId: courseId, title : name }})
    }

    return (
        <div className="max-w-sm w-[200px] h-[280px] bg-white rounded-lg shadow mx-[10px]"
            onClick={navigateToMyCourseDetail}>
            
            <a href="#" onClick={(e) => {
                e.preventDefault();
                navigateToMyCourseDetail();
            }}>
                <img className="rounded-t-lg mx-auto w-full h-[150px] " src={imgUrl} alt="" />
            </a>
        
            <div className="p-5">
                <a href="#" onClick={(e) => {
                    e.preventDefault();
                    navigateToMyCourseDetail();
                }}>
                    <h5 className="mb-2 text-base font-bold tracking-tight text-gray-900 dark:text-white">{name}</h5>
                </a>
                <p className="mb-3 text-sm text-gray-700 dark:text-gray-400">수강생 출력 필요</p>
            </div>
        </div>
    );
}

export default MyCourse;