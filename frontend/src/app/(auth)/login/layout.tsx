export default function Layout({ children }: {children: React.ReactNode}){
    return (
        <div>
            <h1>Login Page</h1>
            {children}
        </div>
    );
}