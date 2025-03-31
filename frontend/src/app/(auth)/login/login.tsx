"use client";
import {FormEvent} from "react";
import {useRouter} from "next/navigation";

export default function Login(){
    const router = useRouter();
    async function handleSubmit(event: FormEvent<HTMLFormElement>){
        event.preventDefault()
        const formData = new FormData(event.currentTarget)
        const email = formData.get('email')
        const password = formData.get('password')

        const response = await fetch('/auth/login' , {
            method: 'POST',
            headers: {'Content-Type' : 'application/json' },
            body: JSON.stringify({email, password}),
        })

        if(response.ok) {
            router.push('/profile')
        }else{

        }
    }

    return (
        <form onSubmit={handleSubmit}>
            <input type = "email" name="email" placeholder="Email" required />
             <input type = "password" name="password" placeholder="Password" required />
            <button type="submit">Login</button>
        </form>
    )
}