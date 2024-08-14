import { useState } from "react";
import { useNavigate, useLocation, useParams } from "react-router-dom";
import { CurriculumInfo } from "../types/CurriculumInfo";
import { useUserStore } from "../store/store";

const CurriculumDetail = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const { nickName } = useUserStore();

  const { courseId } = useParams<{ courseId: string }>();
  const [curriData] = useState<CurriculumInfo>(location.state.data);

  const flag: string = location.state.flag;

  const searchParams = new URLSearchParams(location.search);
  const curriculum_id = Number(searchParams.get("curriculum_id"));

  const handleMakeLive = () => {
    navigate(`/course/live/${courseId}`, {
      state: { memberId: nickName, flag: true, curriId: curriculum_id },
    });
  };

  const handleEnterLive = () => {
    navigate(`/course/live/${courseId}`, {
      state: {
        memberId: nickName,
        flag: false,
        curriId: curriculum_id,
      },
    });
  };

  return (
    <div className="max-w-sm mt-5 ml-5 bg-white border border-gray-200 rounded-lg shadow dark:bg-gray-800 dark:border-gray-700">
      <div className="p-5">
        <h5 className="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
          {curriData.curriculumName}
        </h5>

        <p className="mb-3 font-normal text-gray-700 dark:text-gray-400">{curriData.description}</p>
        {flag === "instructorDetail" && (
          <button
            type="button"
            onClick={handleMakeLive}
            className="inline-flex items-center px-3 py-2 text-sm font-medium text-center text-white bg-blue-700 rounded-lg hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
          >
            실시간 강의 열기
          </button>
        )}
        {flag === "userDetail" && (
          <button
            type="button"
            onClick={handleEnterLive}
            className="inline-flex items-center px-3 py-2 text-sm font-medium text-center text-white bg-blue-700 rounded-lg hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
          >
            실시간 강의 참여
          </button>
        )}
      </div>
    </div>
  );
};

export default CurriculumDetail;
