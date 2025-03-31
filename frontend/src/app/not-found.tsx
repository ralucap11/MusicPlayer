"use client";
import{usePathname} from "next/navigation";

export default function NotFound(){
   const pathname = usePathname();
    return (
        <div>
            <h2>Page Not Found</h2>
            <p>Could not find requested resource</p>
        </div>
    );
}