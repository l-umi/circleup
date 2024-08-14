import { useEffect, useState } from "react";
import { NoticeInfo } from "../../types/NoticeInfo";
import { getNotices } from "../../services/api";
import NoticeCard from "../Card/NoticeCard";

interface NoticeListProps {
    courseId: number,
    isModify : string,
}

const NoticeList = ( { courseId, isModify } : NoticeListProps ) => {
    
    const [notices, setNotices] = useState<NoticeInfo[]>([]);

    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const noticesPerPage = 10;

    useEffect(() => {

        const fetchNotice = async () => {
            try {
                const response = await getNotices(courseId);
                const fetchedNotices = response.data;

                setNotices(fetchedNotices);
                setTotalPages(Math.ceil(fetchedNotices.length / noticesPerPage));
            } catch (error) {
                console.error('Failed to fetch Notices', error);
            }
        }
        fetchNotice();
    }, [courseId]);

    const handlePageChange = (page: number) => {
        setCurrentPage(page);
    };

    const renderNotices = () => {
        const startIndex = (currentPage - 1) * noticesPerPage;
        const selectedNotices = notices.slice(startIndex, startIndex + noticesPerPage);
        
        return selectedNotices.map((notice, idx) => (
            <NoticeCard key={idx} idx={idx+1} data={notice} courseId={courseId} isModify={isModify} />
        ));
    };

    return (
        <div className="notice-list w-full">
            {renderNotices()}

            <nav aria-label="Page navigation example">
                <ul className="inline-flex -space-x-px text-sm m-2">
                    <li>
                        <button className="flex items-center justify-center px-3 h-8 ms-0 leading-tight 
                        text-gray-500 bg-white border border-e-0 border-gray-300 rounded-s-lg
                        hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700
                        dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white">
                            Prev
                        </button>
                    </li>
                    {Array.from({ length: totalPages }, (_, index) => (
                        <li>
                            <button
                                type="button"
                                className="flex items-center justify-center px-3 h-8 leading-tight 
                                text-gray-500 bg-white border border-gray-300 hover:bg-gray-100 
                                hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 
                                dark:hover:bg-gray-700 dark:hover:text-white"
                                key={index}
                                onClick={() => handlePageChange(index + 1)}
                                disabled={currentPage === index + 1}
                            >
                                {index + 1}
                            </button>
                        </li>
                    ))}
                    <li>
                        <button className="flex items-center justify-center px-3 h-8 leading-tight 
                        text-gray-500 bg-white border border-gray-300 rounded-e-lg hover:bg-gray-100
                        hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700
                        dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white">
                            Next
                        </button>
                    </li>
                </ul>
            </nav>
        </div>
    );
}

export default NoticeList;